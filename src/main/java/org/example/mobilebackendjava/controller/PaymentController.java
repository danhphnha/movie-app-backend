package org.example.mobilebackendjava.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.mobilebackendjava.dto.ApiResponse;
import org.example.mobilebackendjava.dto.PaymentInitRequest;
import org.example.mobilebackendjava.security.SecurityUtils;
import org.example.mobilebackendjava.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Authenticated: Client khởi tạo thanh toán VNPay
    @PostMapping("/pay")
    public ApiResponse<Map<String, String>> createPayment(
            @Valid @RequestBody PaymentInitRequest req,
            HttpServletRequest request) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        Map<String, String> result = paymentService.createPaymentUrl(currentUserId, req, request);
        return ApiResponse.ok("Tạo liên kết thanh toán thành công", result);
    }

    // Public: Server-to-Server IPN Webhook từ VNPay
    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, String>> paymentIpn(HttpServletRequest request) {
        Map<String, String> fields = extractVnpayParams(request);
        boolean success = paymentService.processIpnWebhook(fields);

        Map<String, String> response = new HashMap<>();
        if (success) {
            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");
        } else {
            response.put("RspCode", "97");
            response.put("Message", "Invalid Checksum");
        }
        return ResponseEntity.ok(response);
    }

    // Public: Return URL redirect về mobile app via deep link
    @GetMapping("/vnpay-return")
    public void paymentCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> fields = extractVnpayParams(request);
        String redirectUrl = paymentService.processReturnCallback(fields);
        response.sendRedirect(redirectUrl);
    }

    private Map<String, String> extractVnpayParams(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String param = params.nextElement();
            if (param.startsWith("vnp_")) {
                fields.put(param, request.getParameter(param));
            }
        }
        return fields;
    }
}
