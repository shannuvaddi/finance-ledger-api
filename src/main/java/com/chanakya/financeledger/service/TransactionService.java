package com.chanakya.financeledger.service;

import com.chanakya.financeledger.dto.TransactionRequest;
import com.chanakya.financeledger.dto.TransactionResponse;
import com.chanakya.financeledger.entity.Transaction;
import com.chanakya.financeledger.entity.User;
import com.chanakya.financeledger.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<TransactionResponse> getTransactions(User user, LocalDate from, LocalDate to) {
        List<Transaction> transactions;
        if (from != null && to != null) {
            transactions = transactionRepository.findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), from, to);
        } else {
            transactions = transactionRepository.findByUserIdOrderByDateDescCreatedAtDesc(user.getId());
        }
        return transactions.stream().map(TransactionResponse::from).toList();
    }

    public TransactionResponse createTransaction(User user, TransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .description(request.getDescription())
                .amount(request.getAmount())
                .category(request.getCategory())
                .date(request.getDate())
                .user(user)
                .build();

        transaction = transactionRepository.save(transaction);
        return TransactionResponse.from(transaction);
    }

    public TransactionResponse updateTransaction(User user, String id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        if (request.getDate() != null) {
            transaction.setDate(request.getDate());
        }

        transaction = transactionRepository.save(transaction);
        return TransactionResponse.from(transaction);
    }

    public void deleteTransaction(User user, String id) {
        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        transactionRepository.delete(transaction);
    }
}
