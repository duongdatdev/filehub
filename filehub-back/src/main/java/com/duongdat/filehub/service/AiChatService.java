package com.duongdat.filehub.service;

import com.duongdat.filehub.config.GeminiProperties;
import com.duongdat.filehub.dto.request.AiChatRequest;
import com.duongdat.filehub.dto.response.AiChatResponse;
import com.duongdat.filehub.dto.response.FileResponse;
import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.util.SecurityUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AiChatService {

    private final FileService fileService;
    private final GeminiProperties geminiProperties;
    private final SecurityUtil securityUtil;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent";

    public AiChatService(FileService fileService, GeminiProperties geminiProperties, SecurityUtil securityUtil, RestTemplateBuilder restTemplateBuilder) {
        this.fileService = fileService;
        this.geminiProperties = geminiProperties;
        this.securityUtil = securityUtil;
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(60))
                .build();
    }

    /**
     * Process AI chat request and return intelligent file suggestions
     */
    public AiChatResponse processChat(AiChatRequest request) {
        try {
            Long userId = securityUtil.getCurrentUserId()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));

            log.info("Processing AI chat request from user {}: {}", userId, request.getMessage());

            // Parse the user's message to extract intent and keywords
            ChatIntent intent = parseIntent(request.getMessage());
            List<String> keywords = extractKeywords(request.getMessage());

            // Search for relevant files based on intent and keywords
            List<AiChatResponse.FileSuggestion> suggestions = searchFiles(userId, intent, keywords);

            // Generate AI response using Gemini API
            String responseMessage = generateAIResponse(request.getMessage(), suggestions, intent);

            // Generate follow-up suggestions
            List<String> followUpSuggestions = generateFollowUpSuggestions(intent, suggestions);

            // Extract search query for frontend reference
            String searchQuery = extractSearchQuery(request.getMessage());

            return new AiChatResponse(
                    responseMessage,
                    request.getConversationId(),
                    suggestions,
                    followUpSuggestions,
                    searchQuery
            );

        } catch (Exception e) {
            log.error("Error processing AI chat request: {}", e.getMessage(), e);
            return new AiChatResponse(
                    "I'm sorry, I encountered an error while searching for files. Please try again.",
                    request.getConversationId(),
                    Collections.emptyList(),
                    Arrays.asList("Show me recent files", "What files are available?", "Find PDF documents"),
                    null
            );
        }
    }

    /**
     * Parse user intent from the message
     */
    private ChatIntent parseIntent(String message) {
        String lowerMessage = message.toLowerCase();

        if (containsAny(lowerMessage, Arrays.asList("recent", "latest", "new", "today", "yesterday", "this week"))) {
            return ChatIntent.RECENT;
        } else if (containsAny(lowerMessage, Arrays.asList("popular", "most downloaded", "trending", "frequently used"))) {
            return ChatIntent.POPULAR;
        } else if (containsAny(lowerMessage, Arrays.asList("my files", "mine", "uploaded by me", "my documents"))) {
            return ChatIntent.PERSONAL;
        } else if (containsAny(lowerMessage, Arrays.asList("department", "team", "colleagues", "dept"))) {
            return ChatIntent.DEPARTMENT;
        } else if (containsAny(lowerMessage, Arrays.asList("project", "proj"))) {
            return ChatIntent.PROJECT;
        } else {
            return ChatIntent.GENERAL_SEARCH;
        }
    }

    /**
     * Extract keywords from user message
     */
    private List<String> extractKeywords(String message) {
        // Remove common words and extract meaningful keywords
        Set<String> stopWords = Set.of("find", "search", "show", "me", "the", "a", "an", "and", "or", "but", 
                "for", "from", "to", "with", "by", "can", "you", "help", "please", "i", "need", "want", "looking");

        return Arrays.stream(message.toLowerCase().split("\\s+"))
                .filter(word -> word.length() > 2)
                .filter(word -> !stopWords.contains(word))
                .filter(word -> word.matches("[a-zA-Z]+")) // Only alphabetic words
                .distinct()
                .limit(10) // Limit to 10 keywords
                .collect(Collectors.toList());
    }

    /**
     * Search for files based on intent and keywords
     */
    private List<AiChatResponse.FileSuggestion> searchFiles(Long userId, ChatIntent intent, List<String> keywords) {
        try {
            // Build search parameters based on intent
            String filename = keywords.isEmpty() ? null : String.join(" ", keywords);
            
            // Get shared files (the main source for AI chat)
            PageResponse<FileResponse> filesPage = fileService.getSharedFiles(
                    userId, filename, null, null, null, null, null, 
                    0, 20, // Get more files for better scoring
                    "uploadedAt", "DESC"
            );

            List<FileResponse> files = filesPage.getContent();

            // Apply intent-specific filtering
            files = applyIntentFiltering(files, intent, userId);

            // Score files based on relevance
            return scoreFiles(files, keywords, intent)
                    .stream()
                    .limit(6) // Return top 6 suggestions
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error searching files for AI chat: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Apply intent-specific filtering to files
     */
    private List<FileResponse> applyIntentFiltering(List<FileResponse> files, ChatIntent intent, Long userId) {
        switch (intent) {
            case RECENT:
                LocalDateTime oneWeekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
                return files.stream()
                        .filter(file -> file.getUploadedAt() != null && file.getUploadedAt().isAfter(oneWeekAgo))
                        .sorted((f1, f2) -> f2.getUploadedAt().compareTo(f1.getUploadedAt()))
                        .collect(Collectors.toList());

            case POPULAR:
                return files.stream()
                        .filter(file -> file.getDownloadCount() != null && file.getDownloadCount() > 0)
                        .sorted((f1, f2) -> Long.compare(
                                f2.getDownloadCount() != null ? f2.getDownloadCount() : 0L,
                                f1.getDownloadCount() != null ? f1.getDownloadCount() : 0L))
                        .collect(Collectors.toList());

            case PERSONAL:
                return files.stream()
                        .filter(file -> userId.equals(file.getUploaderId()))
                        .collect(Collectors.toList());

            default:
                return files;
        }
    }

    /**
     * Score files based on relevance to keywords and intent
     */
    private List<AiChatResponse.FileSuggestion> scoreFiles(List<FileResponse> files, List<String> keywords, ChatIntent intent) {
        return files.stream()
                .map(file -> {
                    double score = calculateRelevanceScore(file, keywords, intent);
                    String reason = generateReason(file, keywords, intent, score);
                    return new AiChatResponse.FileSuggestion(file, score, reason);
                })
                .filter(suggestion -> suggestion.getRelevanceScore() > 0.1) // Filter out very low scores
                .sorted((s1, s2) -> Double.compare(s2.getRelevanceScore(), s1.getRelevanceScore()))
                .collect(Collectors.toList());
    }

    /**
     * Calculate relevance score for a file
     */
    private double calculateRelevanceScore(FileResponse file, List<String> keywords, ChatIntent intent) {
        double score = 0.0;

        // Base score for all files
        score += 0.1;

        // Score based on filename match
        String filename = (file.getTitle() != null ? file.getTitle() : file.getOriginalFilename()).toLowerCase();
        for (String keyword : keywords) {
            if (filename.contains(keyword.toLowerCase())) {
                score += 0.3;
            }
        }

        // Score based on description match
        if (file.getDescription() != null) {
            String description = file.getDescription().toLowerCase();
            for (String keyword : keywords) {
                if (description.contains(keyword.toLowerCase())) {
                    score += 0.2;
                }
            }
        }

        // Score based on tags match
        if (file.getTags() != null && !file.getTags().isEmpty()) {
            String allTags = String.join(" ", file.getTags()).toLowerCase();
            for (String keyword : keywords) {
                if (allTags.contains(keyword.toLowerCase())) {
                    score += 0.2;
                }
            }
        }

        // Intent-based scoring
        switch (intent) {
            case RECENT:
                if (file.getUploadedAt() != null) {
                    long daysAgo = ChronoUnit.DAYS.between(file.getUploadedAt(), LocalDateTime.now());
                    if (daysAgo < 7) score += 0.3;
                    else if (daysAgo < 30) score += 0.1;
                }
                break;

            case POPULAR:
                if (file.getDownloadCount() != null && file.getDownloadCount() > 0) {
                    score += Math.min(0.3, file.getDownloadCount() * 0.02);
                }
                break;

            case PROJECT:
            case DEPARTMENT:
            case GENERAL_SEARCH:
            case PERSONAL:
                // No additional scoring for these intents
                break;
        }

        // File type popularity boost
        if (file.getContentType() != null) {
            String contentType = file.getContentType().toLowerCase();
            if (contentType.contains("pdf")) score += 0.1;
            else if (contentType.contains("image")) score += 0.05;
            else if (contentType.contains("document") || contentType.contains("word")) score += 0.08;
        }

        return Math.min(1.0, score); // Cap at 1.0
    }

    /**
     * Generate reason for why a file was suggested
     */
    private String generateReason(FileResponse file, List<String> keywords, ChatIntent intent, double score) {
        List<String> reasons = new ArrayList<>();

        // Check for keyword matches
        String filename = (file.getTitle() != null ? file.getTitle() : file.getOriginalFilename()).toLowerCase();
        for (String keyword : keywords) {
            if (filename.contains(keyword.toLowerCase())) {
                reasons.add("contains '" + keyword + "'");
                break; // Only mention one keyword match
            }
        }

        // Intent-specific reasons
        switch (intent) {
            case RECENT:
                if (file.getUploadedAt() != null) {
                    long daysAgo = ChronoUnit.DAYS.between(file.getUploadedAt(), LocalDateTime.now());
                    if (daysAgo < 1) reasons.add("uploaded today");
                    else if (daysAgo < 7) reasons.add("uploaded recently");
                }
                break;

            case POPULAR:
                if (file.getDownloadCount() != null && file.getDownloadCount() > 5) {
                    reasons.add("frequently downloaded");
                }
                break;

            case PERSONAL:
                reasons.add("uploaded by you");
                break;

            case PROJECT:
            case DEPARTMENT:
            case GENERAL_SEARCH:
                // No specific reasons for these intents
                break;
        }

        // File type reason
        if (file.getContentType() != null && file.getContentType().toLowerCase().contains("pdf")) {
            reasons.add("PDF document");
        }

        if (reasons.isEmpty()) {
            return "general match";
        }

        return String.join(", ", reasons);
    }

    /**
     * Generate AI response using Gemini API
     */
    private String generateAIResponse(String userMessage, List<AiChatResponse.FileSuggestion> suggestions, ChatIntent intent) {
        try {
            if (!geminiProperties.isEnabled()) {
                return generateFallbackResponse(userMessage, suggestions, intent);
            }

            String prompt = buildChatPrompt(userMessage, suggestions, intent);
            String geminiResponse = callGeminiAPI(prompt);
            
            if (geminiResponse != null) {
                String extractedResponse = extractResponseFromGemini(geminiResponse);
                if (extractedResponse != null && !extractedResponse.trim().isEmpty()) {
                    return extractedResponse;
                }
            }
            
            log.warn("Failed to get valid response from Gemini API, using fallback");
            return generateFallbackResponse(userMessage, suggestions, intent);
            
        } catch (Exception e) {
            log.error("Error generating AI response with Gemini", e);
            return generateFallbackResponse(userMessage, suggestions, intent);
        }
    }

    /**
     * Call Gemini API for chat response generation
     */
    private String callGeminiAPI(String prompt) {
        int maxRetries = 3;
        int attempt = 0;
        
        while (attempt < maxRetries) {
            try {
                String url = GEMINI_API_URL.replace("{model}", geminiProperties.getModel().getName());
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("x-goog-api-key", geminiProperties.getApi().getKey());
                
                Map<String, Object> requestBody = new HashMap<>();
                Map<String, Object> content = new HashMap<>();
                List<Map<String, Object>> parts = new ArrayList<>();
                
                // Add text part
                Map<String, Object> textPart = new HashMap<>();
                textPart.put("text", prompt);
                parts.add(textPart);
                
                content.put("parts", parts);
                requestBody.put("contents", List.of(content));
                
                // Add generation config
                Map<String, Object> generationConfig = new HashMap<>();
                generationConfig.put("temperature", 0.7); // More creative for conversation
                generationConfig.put("maxOutputTokens", 800); // Shorter responses for chat
                requestBody.put("generationConfig", generationConfig);
                
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
                
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                
                if (response.getStatusCode() == HttpStatus.OK) {
                    return response.getBody();
                } else {
                    log.error("Gemini API returned status: {}", response.getStatusCode());
                    return null;
                }
                
            } catch (HttpServerErrorException.ServiceUnavailable e) {
                attempt++;
                log.warn("Gemini API is overloaded (attempt {}/{}): {}", attempt, maxRetries, e.getMessage());
                
                if (attempt >= maxRetries) {
                    log.error("Gemini API unavailable after {} attempts", maxRetries);
                    return null;
                }
                
                try {
                    long waitTime = (long) Math.pow(2, attempt - 1) * 1000;
                    Thread.sleep(waitTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return null;
                }
                
            } catch (Exception e) {
                log.error("Error calling Gemini API: {}", e.getMessage());
                return null;
            }
        }
        
        return null;
    }

    /**
     * Build prompt for Gemini API
     */
    private String buildChatPrompt(String userMessage, List<AiChatResponse.FileSuggestion> suggestions, ChatIntent intent) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are a helpful AI assistant for a file management system. ");
        prompt.append("A user is asking about files and you need to provide a conversational, helpful response.\n\n");
        
        prompt.append("User Query: \"").append(userMessage).append("\"\n");
        prompt.append("Detected Intent: ").append(intent.toString()).append("\n\n");
        
        if (suggestions.isEmpty()) {
            prompt.append("No files were found matching the user's request.\n\n");
            prompt.append("Please provide a helpful response explaining:\n");
            prompt.append("1. That no files were found matching their criteria\n");
            prompt.append("2. Suggest alternative search terms or approaches\n");
            prompt.append("3. Be encouraging and offer to help with other queries\n");
            prompt.append("4. Keep it friendly and conversational\n");
        } else {
            prompt.append("Found ").append(suggestions.size()).append(" relevant files:\n");
            for (int i = 0; i < Math.min(suggestions.size(), 5); i++) {
                AiChatResponse.FileSuggestion suggestion = suggestions.get(i);
                FileResponse file = suggestion.getFile();
                prompt.append("- ").append(file.getOriginalFilename())
                      .append(" (").append(file.getContentType()).append(")")
                      .append(" - ").append(suggestion.getReason()).append("\n");
            }
            prompt.append("\n");
            
            prompt.append("Please provide a conversational response that:\n");
            prompt.append("1. Confirms you found relevant files\n");
            prompt.append("2. Briefly mentions why these files are relevant\n");
            prompt.append("3. Encourages the user to click on the suggested files\n");
            prompt.append("4. Asks if they need help finding anything else\n");
            prompt.append("5. Keep it friendly and conversational\n");
        }
        
        prompt.append("\nKeep the response conversational, helpful, and under 100 words. ");
        prompt.append("Don't include file lists in your response as they will be shown separately as clickable suggestions. ");
        prompt.append("Use a warm, friendly tone like you're talking to a colleague.");
        
        return prompt.toString();
    }

    /**
     * Extract response text from Gemini API response
     */
    private String extractResponseFromGemini(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode candidatesNode = rootNode.get("candidates");
            
            if (candidatesNode != null && candidatesNode.isArray() && candidatesNode.size() > 0) {
                JsonNode contentNode = candidatesNode.get(0).get("content");
                if (contentNode != null) {
                    JsonNode partsNode = contentNode.get("parts");
                    if (partsNode != null && partsNode.isArray() && partsNode.size() > 0) {
                        return partsNode.get(0).get("text").asText();
                    }
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error extracting response from Gemini API", e);
            return null;
        }
    }

    /**
     * Generate fallback response when Gemini API is not available
     */
    private String generateFallbackResponse(String userMessage, List<AiChatResponse.FileSuggestion> suggestions, ChatIntent intent) {
        if (suggestions.isEmpty()) {
            return generateNoResultsResponse(userMessage, intent);
        } else {
            return generateSuccessResponse(suggestions, intent);
        }
    }

    private String generateSuccessResponse(List<AiChatResponse.FileSuggestion> suggestions, ChatIntent intent) {
        int count = suggestions.size();
        String countText = count == 1 ? "1 file" : count + " files";
        
        switch (intent) {
            case RECENT:
                return String.format("Great! I found %s from your recent uploads. The most relevant one looks promising - click on any file below to view or download it. Need help finding anything else?", countText);
            case POPULAR:
                return String.format("Perfect! I found %s that are popular among users. These are frequently accessed files that might be what you're looking for. Anything else I can help you find?", countText);
            case PERSONAL:
                return String.format("Here are %s that you've uploaded. You can click on any of them to access your files. Looking for something specific among your uploads?", countText);
            default:
                return String.format("Excellent! I found %s that match your search. Click on any file below to view or download it. Is there anything else you'd like me to help you find?", countText);
        }
    }

    /**
     * Generate conversational response message (original fallback method)
     */
    private String generateResponse(String userMessage, List<AiChatResponse.FileSuggestion> suggestions, ChatIntent intent) {
        if (suggestions.isEmpty()) {
            return generateNoResultsResponse(userMessage, intent);
        }

        String greeting = generateGreeting(userMessage, intent);
        String resultSummary = generateResultSummary(suggestions);
        String highlight = generateHighlight(suggestions.get(0));

        return greeting + resultSummary + highlight;
    }

    private String generateGreeting(String userMessage, ChatIntent intent) {
        if (userMessage.toLowerCase().contains("help")) {
            return "I'd be happy to help you find files! ";
        } else if (intent == ChatIntent.RECENT) {
            return "Here are the recent files: ";
        } else if (intent == ChatIntent.POPULAR) {
            return "Here are the most popular files: ";
        } else {
            return "Based on your search, ";
        }
    }

    private String generateResultSummary(List<AiChatResponse.FileSuggestion> suggestions) {
        int count = suggestions.size();
        if (count == 1) {
            return "I found 1 file that matches your search. ";
        } else {
            return String.format("I found %d files that match your search. ", count);
        }
    }

    private String generateHighlight(AiChatResponse.FileSuggestion topSuggestion) {
        String filename = topSuggestion.getFile().getTitle() != null ? 
                topSuggestion.getFile().getTitle() : topSuggestion.getFile().getOriginalFilename();
        return String.format("The most relevant one is '%s' because it %s.", filename, topSuggestion.getReason());
    }

    private String generateNoResultsResponse(String userMessage, ChatIntent intent) {
        switch (intent) {
            case RECENT:
                return "I couldn't find any recent files. Try checking if files have been uploaded recently or adjust your search terms.";
            case POPULAR:
                return "I couldn't find any popular files. Try browsing all available files or use different search terms.";
            case PERSONAL:
                return "I couldn't find any files uploaded by you. Try uploading some files first or check your file access permissions.";
            default:
                return String.format("I couldn't find any files matching '%s'. Try using different keywords or check if the files are shared with you.", 
                        extractSearchQuery(userMessage));
        }
    }

    /**
     * Generate follow-up suggestions
     */
    private List<String> generateFollowUpSuggestions(ChatIntent intent, List<AiChatResponse.FileSuggestion> suggestions) {
        List<String> followUps = new ArrayList<>();

        if (!suggestions.isEmpty()) {
            followUps.add("Show me more files from the same department");
            followUps.add("Find files uploaded this week");
            followUps.add("What are the most downloaded files?");
        } else {
            followUps.add("Show me all recent files");
            followUps.add("What files are available in my department?");
            followUps.add("Find the most popular files");
        }

        return followUps.stream().limit(3).collect(Collectors.toList());
    }

    /**
     * Extract clean search query from conversational message
     */
    private String extractSearchQuery(String message) {
        String[] conversationalPhrases = {
                "can you help me find", "i need to find", "show me", "search for",
                "find me", "looking for", "where is", "do you have"
        };

        String cleanQuery = message.toLowerCase();
        for (String phrase : conversationalPhrases) {
            cleanQuery = cleanQuery.replace(phrase, "").trim();
        }

        return cleanQuery.isEmpty() ? message : cleanQuery;
    }

    /**
     * Helper method to check if message contains any of the given phrases
     */
    private boolean containsAny(String message, List<String> phrases) {
        return phrases.stream().anyMatch(message::contains);
    }

    /**
     * Enum for different chat intents
     */
    public enum ChatIntent {
        RECENT, POPULAR, PERSONAL, DEPARTMENT, PROJECT, GENERAL_SEARCH
    }
}
