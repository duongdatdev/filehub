package com.duongdat.filehub.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequest {
    private String message;
    private String conversationId;
    private String intent; // search, recent, popular, etc.
}
