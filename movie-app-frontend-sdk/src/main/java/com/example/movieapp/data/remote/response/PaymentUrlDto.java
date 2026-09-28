package com.example.movieapp.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class PaymentUrlDto {

    @SerializedName("paymentUrl")
    private String paymentUrl;

    @SerializedName("transactionId")
    private String transactionId;

    public PaymentUrlDto() {}

    public PaymentUrlDto(String paymentUrl, String transactionId) {
        this.paymentUrl = paymentUrl;
        this.transactionId = transactionId;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
