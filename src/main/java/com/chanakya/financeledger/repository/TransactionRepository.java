package com.chanakya.financeledger.repository;

import com.chanakya.financeledger.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByUserIdOrderByDateDescCreatedAtDesc(String userId);

    List<Transaction> findByUserIdAndDateBetweenOrderByDateDesc(String userId, LocalDate from, LocalDate to);

    Optional<Transaction> findByIdAndUserId(String id, String userId);
}
