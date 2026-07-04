package com.chanakya.financeledger.voice.domain;

import com.chanakya.financeledger.auth.domain.User;
import com.chanakya.financeledger.chat.domain.ChatService;
import com.chanakya.financeledger.chat.dto.ChatResponse;
import com.chanakya.financeledger.voice.dto.VoiceTranscribeResponse;
import com.chanakya.financeledger.voice.infrastructure.rest.VoiceTranscriptionClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class VoiceService {

    private final VoiceTranscriptionClient transcriptionClient;
    private final ChatService chatService;

    public VoiceTranscribeResponse transcribeAndProcess(User user, MultipartFile audioFile) {
        String transcription = transcriptionClient.transcribe(audioFile);

        ChatResponse chatResponse = chatService.processMessage(user, transcription);

        return VoiceTranscribeResponse.builder()
                .transcription(transcription)
                .chat(chatResponse)
                .build();
    }
}
