package com.chanakya.financeledger.controller;

import com.chanakya.financeledger.dto.ChatRequest;
import com.chanakya.financeledger.dto.ChatResponse;
import com.chanakya.financeledger.entity.User;
import com.chanakya.financeledger.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.processMessage(user, request.getContent()));
    }
}
