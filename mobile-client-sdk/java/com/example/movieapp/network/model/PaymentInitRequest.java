package com.example.movieapp.network.model;

import com.google.gson.annotations.SerializedName;

public class PaymentInitRequest {
    @SerializedName("amount")
    private int amount;

    @SerializedName("orderInfo")
    private String orderInfo;

    public PaymentInitRequest() {}

    public PaymentInitRequest(int amount, String orderInfo) {
        this.amount = amount;
        this.orderInfo = orderInfo;
    }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getOrderInfo() { return orderInfo; }
    public void setOrderInfo(String orderInfo) { this.orderInfo = orderInfo; }
}
