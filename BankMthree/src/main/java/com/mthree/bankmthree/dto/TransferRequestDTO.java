package com.mthree.bankmthree.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDTO {

    @NotBlank(message = "Sender account number is required")
    private String senderCardNumber;

    @NotBlank(message = "Receiver account number is required")
    private String receiverCardNumber;

    @DecimalMin(value = "0.01", inclusive = true, message = "Transfer amount must be greater than zero")
    private BigDecimal amount;
}