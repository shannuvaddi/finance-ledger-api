package com.chanakya.financeledger.voice;

import com.chanakya.financeledger.auth.domain.User;
import com.chanakya.financeledger.voice.domain.VoiceService;
import com.chanakya.financeledger.voice.dto.VoiceTranscribeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/voice")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceService voiceService;

    @PostMapping("/transcribe")
    public ResponseEntity<VoiceTranscribeResponse> transcribe(
            @AuthenticationPrincipal User user,
            @RequestParam("audio") MultipartFile audioFile) {
        return ResponseEntity.ok(voiceService.transcribeAndProcess(user, audioFile));
    }
}
