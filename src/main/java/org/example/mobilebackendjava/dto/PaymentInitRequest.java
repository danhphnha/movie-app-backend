package org.example.mobilebackendjava.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentInitRequest {
    @NotNull(message = "Amount is required")
    @Min(value = 1000, message = "Minimum amount is 1,000 VND")
    private Integer amount;

    private String orderInfo;

    public PaymentInitRequest() {}

    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }

    public String getOrderInfo() { return orderInfo; }
    public void setOrderInfo(String orderInfo) { this.orderInfo = orderInfo; }
}
