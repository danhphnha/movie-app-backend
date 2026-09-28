package org.example.mobilebackendjava.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.mobilebackendjava.controller.HmacUtil;
import org.example.mobilebackendjava.dto.PaymentInitRequest;
import org.example.mobilebackendjava.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class PaymentService {

    private static final String PAYMENTS_COLLECTION = "payments";

    @Value("${vnpay.tmnCode}")
    private String vnpTmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnpHashSecret;

    @Value("${vnpay.payUrl}")
    private String vnpPayUrl;

    @Value("${vnpay.returnUrl}")
    private String vnpReturnUrl;

    private final Firestore db;

    public PaymentService(Firestore db) {
        this.db = db;
    }

    public Map<String, String> createPaymentUrl(String userId, PaymentInitRequest req, HttpServletRequest request) {
        String txnRef = UUID.randomUUID().toString().replace("-", "").substring(0, 8) + "-" + userId;
        String orderInfo = req.getOrderInfo() != null && !req.getOrderInfo().trim().isEmpty()
                ? req.getOrderInfo().trim()
                : "Thanh toan don hang " + txnRef;

        int amountInVND = req.getAmount() * 100;

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnpTmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(amountInVND));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", orderInfo);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnpReturnUrl);
        vnpParams.put("vnp_IpAddr", getClientIpAddr(request));

        String vnpCreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        vnpParams.put("vnp_CreateDate", vnpCreateDate);

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String value = vnpParams.get(fieldName);
            if (value != null && !value.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append('&');
                query.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append('&');
            }
        }

        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        String secureHash = HmacUtil.hmacSHA512(vnpHashSecret, hashData.toString());
        String paymentUrl = vnpPayUrl + "?" + query + "&vnp_SecureHash=" + secureHash;

        // Record initial PENDING payment state in DB for idempotency tracking
        savePendingPayment(txnRef, req.getAmount(), userId, "VNpay");

        log.info("Generated VNPay URL for txnRef: {}, userId: {}", txnRef, userId);

        Map<String, String> result = new HashMap<>();
        result.put("paymentUrl", paymentUrl);
        result.put("transactionId", txnRef);
        return result;
    }

    public boolean processIpnWebhook(Map<String, String> fields) {
        String vnpSecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String signValue = HmacUtil.hmacSHA512(vnpHashSecret, buildHashData(fields));
        if (!signValue.equalsIgnoreCase(vnpSecureHash)) {
            log.warn("Invalid VNPay IPN signature received");
            return false;
        }

        String responseCode = fields.get("vnp_ResponseCode");
        String txnRefWithUser = fields.get("vnp_TxnRef");
        String[] parts = txnRefWithUser.split("-", 2);
        String txnRef = parts[0];
        String userId = parts.length > 1 ? parts[1] : null;

        int amountInVND = Integer.parseInt(fields.get("vnp_Amount")) / 100;
        boolean isSuccess = "00".equals(responseCode);

        updatePaymentStatus(txnRefWithUser, amountInVND, isSuccess, "VNpay", userId);
        return true;
    }

    public String processReturnCallback(Map<String, String> fields) {
        String vnpSecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String signValue = HmacUtil.hmacSHA512(vnpHashSecret, buildHashData(fields));
        if (!signValue.equalsIgnoreCase(vnpSecureHash)) {
            log.warn("Invalid VNPay return signature");
            return "movieapp://payment-invalid";
        }

        String responseCode = fields.get("vnp_ResponseCode");
        String txnRefWithUser = fields.get("vnp_TxnRef");
        String[] parts = txnRefWithUser.split("-", 2);
        String userId = parts.length > 1 ? parts[1] : null;
        int amountInVND = Integer.parseInt(fields.get("vnp_Amount")) / 100;

        boolean isSuccess = "00".equals(responseCode);
        updatePaymentStatus(txnRefWithUser, amountInVND, isSuccess, "VNpay", userId);

        return isSuccess ? "movieapp://payment-success" : "movieapp://payment-failed";
    }

    private void savePendingPayment(String transactionId, int amount, String userId, String paymentMethod) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("transactionId", transactionId);
            data.put("amount", amount);
            data.put("paid", false);
            data.put("status", "PENDING");
            data.put("paymentMethod", paymentMethod);
            data.put("paymentTime", new Date());
            data.put("userId", userId);

            db.collection(PAYMENTS_COLLECTION).document(transactionId).set(data).get();
        } catch (Exception e) {
            log.error("Failed to record pending payment for {}", transactionId, e);
        }
    }

    private void updatePaymentStatus(String transactionId, int amount, boolean paid, String paymentMethod, String userId) {
        try {
            DocumentReference docRef = db.collection(PAYMENTS_COLLECTION).document(transactionId);
            DocumentSnapshot snapshot = docRef.get().get();

            if (snapshot.exists() && Boolean.TRUE.equals(snapshot.getBoolean("paid"))) {
                log.info("Payment {} was already finalized. Skipping duplicate update.", transactionId);
                return;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("transactionId", transactionId);
            data.put("amount", amount);
            data.put("paid", paid);
            data.put("status", paid ? "SUCCESS" : "FAILED");
            data.put("paymentMethod", paymentMethod);
            data.put("paymentTime", new Date());
            data.put("userId", userId);

            docRef.set(data, SetOptions.merge()).get();
            log.info("Updated payment {} status to paid={}", transactionId, paid);
        } catch (Exception e) {
            log.error("Failed to update payment status for {}", transactionId, e);
            throw new AppException("Failed to update payment record", e);
        }
    }

    private String buildHashData(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String field : fieldNames) {
            String val = params.get(field);
            if (val != null && !val.isEmpty()) {
                hashData.append(field).append('=').append(URLEncoder.encode(val, StandardCharsets.UTF_8)).append('&');
            }
        }
        if (hashData.length() > 0) {
            hashData.setLength(hashData.length() - 1);
        }
        return hashData.toString();
    }

    private String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "127.0.0.1";
    }
}
