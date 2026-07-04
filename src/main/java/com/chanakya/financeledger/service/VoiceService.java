package com.chanakya.financeledger.service;

import com.chanakya.financeledger.dto.ChatResponse;
import com.chanakya.financeledger.dto.VoiceTranscribeResponse;
import com.chanakya.financeledger.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class VoiceService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final String apiKey;

    public VoiceService(@Value("${app.gemini.api-key}") String apiKey,
                        ObjectMapper objectMapper,
                        ChatService chatService) {
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
        this.chatService = chatService;
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
    }

    public VoiceTranscribeResponse transcribeAndProcess(User user, MultipartFile audioFile) {
        String transcription = transcribe(audioFile);

        ChatResponse chatResponse = chatService.processMessage(user, transcription);

        return VoiceTranscribeResponse.builder()
                .transcription(transcription)
                .chat(chatResponse)
                .build();
    }

    private String transcribe(MultipartFile audioFile) {
        try {
            byte[] audioBytes = audioFile.getBytes();
            String base64Audio = Base64.getEncoder().encodeToString(audioBytes);

            String mimeType = audioFile.getContentType() != null
                    ? audioFile.getContentType()
                    : "audio/webm";

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", "Transcribe this audio exactly. Return only the transcription, nothing else."),
                                    Map.of("inline_data", Map.of(
                                            "mime_type", mimeType,
                                            "data", base64Audio
                                    ))
                            ))
                    )
            );

            String responseBody = restClient.post()
                    .uri("/models/gemini-2.0-flash:generateContent?key={key}", apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText("Could not transcribe audio");

        } catch (Exception e) {
            log.error("Failed to transcribe audio", e);
            throw new RuntimeException("Audio transcription failed");
        }
    }
}
