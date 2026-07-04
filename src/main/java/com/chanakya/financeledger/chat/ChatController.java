package com.chanakya.financeledger.chat;

import com.chanakya.financeledger.auth.domain.User;
import com.chanakya.financeledger.chat.domain.ChatService;
import com.chanakya.financeledger.chat.dto.ChatRequest;
import com.chanakya.financeledger.chat.dto.ChatResponse;
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
