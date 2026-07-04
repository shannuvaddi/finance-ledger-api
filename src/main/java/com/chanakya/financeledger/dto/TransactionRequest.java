package com.chanakya.financeledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {

    @NotBlank
    private String description;

    @NotNull
    private BigDecimal amount;

    private String category;

    private LocalDate date;
}
