package com.duongdat.filehub.controller;

import com.duongdat.filehub.dto.request.AiChatRequest;
import com.duongdat.filehub.dto.response.AiChatResponse;
import com.duongdat.filehub.service.AiChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class AiChatController {

    private final AiChatService aiChatService;

    /**
     * Handle AI chat requests for file search
     */
    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@RequestBody AiChatRequest request) {
        try {
            log.info("Received AI chat request: {}", request.getMessage());
            AiChatResponse response = aiChatService.processChat(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing AI chat request", e);
            return ResponseEntity.internalServerError()
                    .body(AiChatResponse.builder()
                            .message("Sorry, I encountered an error while processing your request. Please try again.")
                            .suggestions(java.util.Collections.emptyList())
                            .conversationId(request.getConversationId())
                            .build());
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Chat service is running");
    }
}
