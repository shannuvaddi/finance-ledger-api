package com.chanakya.financeledger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceTranscribeResponse {

    private String transcription;
    private ChatResponse chat;
}
