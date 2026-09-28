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
}
