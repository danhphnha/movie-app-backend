package com.example.movieapp.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class InitPaymentRequest {

    @SerializedName("amount")
    private int amount;

    @SerializedName("orderInfo")
    private String orderInfo;

    public InitPaymentRequest() {}

    public InitPaymentRequest(int amount, String orderInfo) {
        this.amount = amount;
        this.orderInfo = orderInfo;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }
}
