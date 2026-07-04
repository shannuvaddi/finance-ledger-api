package com.chanakya.financeledger.service;

import com.chanakya.financeledger.dto.ChatResponse;
import com.chanakya.financeledger.dto.TransactionRequest;
import com.chanakya.financeledger.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final GeminiService geminiService;
    private final TransactionService transactionService;

    public ChatResponse processMessage(User user, String content) {
        ChatResponse response = geminiService.processMessage(content);

        if (response.getTransaction() != null && response.getTransaction().getAmount() != null) {
            TransactionRequest txRequest = new TransactionRequest();
            txRequest.setDescription(response.getTransaction().getDescription());
            txRequest.setAmount(response.getTransaction().getAmount());
            txRequest.setCategory(response.getTransaction().getCategory());
            txRequest.setDate(response.getTransaction().getDate());

            transactionService.createTransaction(user, txRequest);
        }

        return response;
    }
}
