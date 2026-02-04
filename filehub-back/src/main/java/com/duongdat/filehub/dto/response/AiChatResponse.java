package com.duongdat.filehub.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiChatResponse {
    private String message;
    private String conversationId;
    private List<FileSuggestion> suggestions;
    private List<String> followUpSuggestions;
    private String searchQuery;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileSuggestion {
        private FileResponse file;
        private Double relevanceScore;
        private String reason;
    }
}
