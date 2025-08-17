package com.duongdat.filehub.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileAnalysisResponse {
    private String summary;
    private String category;
    private List<String> tags;
    private String suggestedDepartment;
    private String suggestedProject;
    private String contentType;
    private String language;
    private Double confidenceScore;
    private List<String> keyTopics;
    private String priority;
    private List<String> relatedFiles;
    private String storageRecommendation;
    private String accessRecommendation;
    
    // Enhanced fields
    private String suggestedTitle;
    private String suggestedDescription;
    private Long suggestedFileTypeId;
    private String suggestedFileTypeName;
    private Long suggestedDepartmentCategoryId;
    private String suggestedDepartmentCategoryName;
    private String suggestedVisibility; // PRIVATE, DEPARTMENT, PUBLIC
    private String suggestedPriority; // LOW, MEDIUM, HIGH
    
    // Content analysis details
    private ContentAnalysis contentAnalysis;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentAnalysis {
        private String documentType;
        private String technicalLevel; // BASIC, INTERMEDIATE, ADVANCED
        private String estimatedImportance; // LOW, MEDIUM, HIGH
        private List<String> suggestedAccess;
        private List<String> relatedKeywords;
        private String analysisMethod; // files_api_analysis, metadata_only
    }
    
    public static FileAnalysisResponse createDefault(String fileName) {
        FileAnalysisResponse response = new FileAnalysisResponse();
        response.setSummary("File analysis not available");
        response.setCategory("General");
        response.setTags(List.of("file"));
        response.setConfidenceScore(0.0);
        response.setPriority("Normal");
        response.setSuggestedPriority("MEDIUM");
        response.setSuggestedVisibility("PRIVATE");
        return response;
    }
}