package org.example.mobilebackendjava.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import lombok.extern.slf4j.Slf4j;
import org.example.mobilebackendjava.exception.AppException;
import org.example.mobilebackendjava.model.Payment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class RevenueService {

    private static final String PAYMENTS_COLLECTION = "payments";
    private final Firestore firestore;

    public RevenueService(Firestore firestore) {
        this.firestore = firestore;
    }

    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(PAYMENTS_COLLECTION).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot doc : documents) {
                try {
                    Payment payment = new Payment();
                    payment.setId(doc.getId());

                    Long amountVal = doc.getLong("amount");
                    payment.setAmount(amountVal != null ? amountVal.intValue() : 0);

                    Boolean paidVal = doc.getBoolean("paid");
                    payment.setPaid(Boolean.TRUE.equals(paidVal));

                    payment.setPaymentMethod(doc.getString("paymentMethod"));

                    com.google.cloud.Timestamp ts = doc.getTimestamp("paymentTime");
                    if (ts != null) {
                        payment.setPaymentTime(ts.toDate());
                    }

                    payment.setUserId(doc.getString("userId"));
                    payments.add(payment);
                } catch (Exception e) {
                    log.warn("Error parsing payment record {}: {}", doc.getId(), e.getMessage());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error retrieving payments from Firestore", e);
            throw new AppException("Failed to retrieve payments data", e);
        }
        return payments;
    }

    public long getTotalRevenue() {
        long total = 0;
        for (Payment p : getAllPayments()) {
            if (p.isPaid()) {
                total += p.getAmount();
            }
        }
        return total;
    }
}
