package com.chanakya.financeledger.transaction.dto;

import com.chanakya.financeledger.transaction.domain.Transaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionResponse {

    private String id;
    private String description;
    private BigDecimal amount;
    private String category;
    private LocalDate date;

    public static TransactionResponse from(Transaction tx) {
        return TransactionResponse.builder()
                .id(tx.getId())
                .description(tx.getDescription())
                .amount(tx.getAmount())
                .category(tx.getCategory())
                .date(tx.getDate())
                .build();
    }
}
