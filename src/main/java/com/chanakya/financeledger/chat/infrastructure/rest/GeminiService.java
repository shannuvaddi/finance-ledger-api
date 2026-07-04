package com.chanakya.financeledger.chat.infrastructure.rest;

import com.chanakya.financeledger.chat.dto.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeminiService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public GeminiService(@Value("${app.gemini.api-key}") String apiKey,
                         ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
    }

    public ChatResponse processMessage(String userMessage) {
        String systemPrompt = """
                You are Chanakya, a personal finance assistant. Your job is to help users log expenses and income through natural language.

                When a user mentions a financial transaction, extract the following and respond with BOTH a friendly confirmation AND a JSON block:
                - description: what the transaction is for
                - amount: the numeric amount (positive for income, negative for expenses)
                - category: one of [food, transport, shopping, bills, entertainment, health, education, salary, freelance, other]
                - date: the date of the transaction (use today's date if not specified)

                Today's date is %s.

                IMPORTANT: If you detect a transaction, you MUST include a JSON block in your response wrapped in ```json ... ``` markers. Example:

                User: "spent 500 on groceries yesterday"
                Response: Got it! I've logged ₹500 for groceries yesterday.
                ```json
                {"description": "groceries", "amount": -500, "category": "food", "date": "2024-01-14"}
                ```

                If the user is just chatting or asking questions about finance, respond helpfully without a JSON block.
                """.formatted(LocalDate.now().toString());

        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of(
                        "parts", List.of(Map.of("text", systemPrompt))
                ),
                "contents", List.of(
                        Map.of("role", "user", "parts", List.of(Map.of("text", userMessage)))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "maxOutputTokens", 512
                )
        );

        String responseBody = restClient.post()
                .uri("/models/gemini-2.0-flash:generateContent?key={key}", apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(String.class);

        return parseGeminiResponse(responseBody);
    }

    private ChatResponse parseGeminiResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");

            if (candidates.isEmpty()) {
                return ChatResponse.builder().reply("I couldn't process that. Please try again.").build();
            }

            String text = candidates.get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            ChatResponse.TransactionData transaction = extractTransaction(text);

            String reply = text.replaceAll("```json\\s*\\{[^}]*}\\s*```", "").trim();

            return ChatResponse.builder()
                    .reply(reply)
                    .transaction(transaction)
                    .build();

        } catch (Exception e) {
            log.error("Failed to parse Gemini response", e);
            return ChatResponse.builder().reply("Something went wrong processing your message.").build();
        }
    }

    private ChatResponse.TransactionData extractTransaction(String text) {
        try {
            int jsonStart = text.indexOf("```json");
            if (jsonStart == -1) return null;

            int contentStart = text.indexOf("{", jsonStart);
            int contentEnd = text.indexOf("}", contentStart);
            if (contentStart == -1 || contentEnd == -1) return null;

            String jsonStr = text.substring(contentStart, contentEnd + 1);
            JsonNode json = objectMapper.readTree(jsonStr);

            return ChatResponse.TransactionData.builder()
                    .description(json.path("description").asText(null))
                    .amount(json.has("amount") ? BigDecimal.valueOf(json.path("amount").asDouble()) : null)
                    .category(json.path("category").asText(null))
                    .date(json.has("date") ? LocalDate.parse(json.path("date").asText()) : LocalDate.now())
                    .build();

        } catch (Exception e) {
            log.warn("Failed to extract transaction from Gemini response", e);
            return null;
        }
    }
}
