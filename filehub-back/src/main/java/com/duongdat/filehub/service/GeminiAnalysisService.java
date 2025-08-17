package com.duongdat.filehub.service;

import com.duongdat.filehub.config.GeminiProperties;
import com.duongdat.filehub.dto.request.FileAnalysisRequest;
import com.duongdat.filehub.dto.response.FileAnalysisResponse;
import com.duongdat.filehub.entity.Department;
import com.duongdat.filehub.entity.DepartmentCategory;
import com.duongdat.filehub.entity.FileType;
import com.duongdat.filehub.entity.Project;
import com.duongdat.filehub.repository.DepartmentCategoryRepository;
import com.duongdat.filehub.repository.DepartmentRepository;
import com.duongdat.filehub.repository.FileTypeRepository;
import com.duongdat.filehub.repository.ProjectRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GeminiAnalysisService {
    
    private final GeminiProperties geminiProperties;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;
    private final FileTypeRepository fileTypeRepository;
    private final DepartmentCategoryRepository departmentCategoryRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public GeminiAnalysisService(GeminiProperties geminiProperties,
                                DepartmentRepository departmentRepository,
                                ProjectRepository projectRepository,
                                FileTypeRepository fileTypeRepository,
                                DepartmentCategoryRepository departmentCategoryRepository,
                                RestTemplateBuilder restTemplateBuilder) {
        this.geminiProperties = geminiProperties;
        this.departmentRepository = departmentRepository;
        this.projectRepository = projectRepository;
        this.fileTypeRepository = fileTypeRepository;
        this.departmentCategoryRepository = departmentCategoryRepository;
        // Configure RestTemplate with timeouts for AI API calls
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(30))
                .setReadTimeout(Duration.ofSeconds(120)) // 2 minutes for AI processing
                .build();
    }
    
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent";
    
    public FileAnalysisResponse analyzeFile(FileAnalysisRequest request) {
        if (!geminiProperties.isEnabled()) {
            log.info("Gemini AI is disabled, returning default analysis");
            return FileAnalysisResponse.createDefault(request.getFileName());
        }
        
        try {
            String prompt = buildAnalysisPrompt(request);
            String geminiResponse = callGeminiAPI(prompt);
            return parseGeminiResponse(geminiResponse, request);
            
        } catch (Exception e) {
            log.error("Error analyzing file with Gemini AI: {}", e.getMessage(), e);
            return FileAnalysisResponse.createDefault(request.getFileName());
        }
    }
    
    private String buildAnalysisPrompt(FileAnalysisRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        // Get department and project context
        String departmentContext = getDepartmentContext(request.getDepartmentId());
        String projectContext = getProjectContext(request.getProjectId());
        
        // Determine analysis approach based on file size
        boolean isLargeFile = request.getFileSize() != null && request.getFileSize() > geminiProperties.getMaxFileSize();
        boolean hasContent = request.getFileContent() != null && !request.getFileContent().trim().isEmpty();
        
        if (isLargeFile) {
            prompt.append("Analyze the following large file using metadata only (content analysis not available due to file size):\n\n");
        } else if (hasContent) {
            prompt.append("Analyze the following file using both metadata and content:\n\n");
        } else {
            prompt.append("Analyze the following file using available metadata:\n\n");
        }
        
        prompt.append("File Name: ").append(request.getFileName()).append("\n");
        prompt.append("Content Type: ").append(request.getContentType()).append("\n");
        
        // Add file size information
        if (request.getFileSize() != null) {
            String sizeDisplay = formatFileSize(request.getFileSize());
            prompt.append("File Size: ").append(sizeDisplay).append("\n");
        }
        
        // Add title if available
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            prompt.append("File Title: ").append(request.getTitle()).append("\n");
        }
        
        if (departmentContext != null) {
            prompt.append("Current Department Context: ").append(departmentContext).append("\n");
        }
        
        if (projectContext != null) {
            prompt.append("Current Project Context: ").append(projectContext).append("\n");
        }
        
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            prompt.append("File Description: ").append(request.getDescription()).append("\n");
        }
        
        // Include content for small files only
        if (!isLargeFile && hasContent) {
            prompt.append("File Content:\n").append(request.getFileContent()).append("\n");
        } else if (isLargeFile) {
            prompt.append("\nNote: This is a large file (").append(formatFileSize(request.getFileSize()))
                  .append("). Analysis is based on metadata, filename, title, and description only.\n");
        }
        
        prompt.append("\nPlease provide a JSON response with the following structure:\n");
        prompt.append("{\n");
        prompt.append("  \"summary\": \"Brief summary of the file based on available information\",\n");
        prompt.append("  \"category\": \"Document category (e.g., Report, Presentation, Code, Data, etc.)\",\n");
        prompt.append("  \"tags\": [\"relevant\", \"tags\", \"for\", \"search\"],\n");
        prompt.append("  \"suggestedTitle\": \"Smart title based on available information\",\n");
        prompt.append("  \"suggestedDescription\": \"Descriptive summary of the document\",\n");
        prompt.append("  \"suggestedDepartment\": \"Most appropriate department name\",\n");
        prompt.append("  \"suggestedProject\": \"Most appropriate project name if any\",\n");
        prompt.append("  \"suggestedFileTypeName\": \"Most appropriate file type (Document, Report, Presentation, etc.)\",\n");
        prompt.append("  \"suggestedDepartmentCategoryName\": \"Most appropriate category within the suggested department\",\n");
        prompt.append("  \"suggestedVisibility\": \"PRIVATE/DEPARTMENT/PUBLIC - based on content sensitivity\",\n");
        prompt.append("  \"suggestedPriority\": \"LOW/MEDIUM/HIGH - based on content importance\",\n");
        prompt.append("  \"language\": \"Primary language of content\",\n");
        prompt.append("  \"confidenceScore\": 0.95,\n");
        prompt.append("  \"keyTopics\": [\"main\", \"topics\", \"covered\"],\n");
        prompt.append("  \"priority\": \"High/Medium/Low\",\n");
        prompt.append("  \"relatedFiles\": [\"potential\", \"related\", \"file\", \"names\"],\n");
        prompt.append("  \"storageRecommendation\": \"Suggestion for storage location\",\n");
        prompt.append("  \"accessRecommendation\": \"Who should have access to this file\",\n");
        prompt.append("  \"contentAnalysis\": {\n");
        prompt.append("    \"documentType\": \"meeting/report/planning/documentation/policy/financial/etc\",\n");
        prompt.append("    \"technicalLevel\": \"BASIC/INTERMEDIATE/ADVANCED\",\n");
        prompt.append("    \"estimatedImportance\": \"LOW/MEDIUM/HIGH\",\n");
        prompt.append("    \"suggestedAccess\": [\"roles\", \"who\", \"should\", \"access\"],\n");
        prompt.append("    \"relatedKeywords\": [\"contextual\", \"keywords\"],\n");
        prompt.append("    \"analysisMethod\": \"").append(isLargeFile ? "metadata_only" : (hasContent ? "content_and_metadata" : "metadata_only")).append("\"\n");
        prompt.append("  }\n");
        prompt.append("}\n\n");
        
        // Add available departments, projects, file types, and categories for context
        List<Department> departments = departmentRepository.findByIsActiveTrueOrderByName();
        List<Project> projects = projectRepository.findAllByOrderByCreatedAtDesc();
        List<FileType> fileTypes = fileTypeRepository.findAll();
        List<DepartmentCategory> categories = departmentCategoryRepository.findAll();
        
        if (!departments.isEmpty()) {
            prompt.append("Available Departments:\n");
            departments.forEach(dept -> prompt.append("- ").append(dept.getName())
                    .append(dept.getDescription() != null ? ": " + dept.getDescription() : "").append("\n"));
        }
        
        if (!projects.isEmpty()) {
            prompt.append("\nAvailable Projects:\n");
            projects.stream().limit(20).forEach(project -> prompt.append("- ").append(project.getName())
                    .append(project.getDescription() != null ? ": " + project.getDescription() : "").append("\n"));
        }
        
        if (!fileTypes.isEmpty()) {
            prompt.append("\nAvailable File Types:\n");
            fileTypes.forEach(fileType -> prompt.append("- ").append(fileType.getName())
                    .append(fileType.getDescription() != null ? ": " + fileType.getDescription() : "").append("\n"));
        }
        
        if (!categories.isEmpty()) {
            prompt.append("\nAvailable Department Categories:\n");
            categories.forEach(category -> prompt.append("- ").append(category.getName())
                    .append(" (Dept: ").append(category.getDepartment().getName()).append(")")
                    .append(category.getDescription() != null ? ": " + category.getDescription() : "").append("\n"));
        }
        
        prompt.append("\nAnalysis Guidelines:\n");
        if (isLargeFile) {
            prompt.append("- This is a large file, so base your analysis on filename, title, description, file type, and size\n");
            prompt.append("- Infer content type and purpose from file extension and metadata\n");
            prompt.append("- For document files (.docx, .pdf, etc.), suggest appropriate business categories\n");
            prompt.append("- For media files, focus on storage and access recommendations\n");
            prompt.append("- Lower confidence scores are acceptable due to limited information\n");
        } else if (hasContent) {
            prompt.append("- Use both file content and metadata for comprehensive analysis\n");
            prompt.append("- Extract key topics and themes from the actual content\n");
            prompt.append("- Provide high confidence scores when content is clear\n");
        } else {
            prompt.append("- Base analysis on available metadata (filename, title, description)\n");
            prompt.append("- Make reasonable inferences about content based on file type\n");
        }
        
        prompt.append("- When suggesting a department category, make sure it belongs to the suggested department\n");
        prompt.append("- For visibility: PRIVATE for sensitive/personal content, DEPARTMENT for team sharing, PUBLIC for general access\n");
        prompt.append("- For priority: HIGH for urgent/critical documents, MEDIUM for normal business documents, LOW for reference materials\n");
        
        return prompt.toString();
    }
    
    private String getDepartmentContext(Long departmentId) {
        if (departmentId == null) return null;
        
        return departmentRepository.findById(departmentId)
                .map(dept -> dept.getName() + 
                        (dept.getDescription() != null ? " - " + dept.getDescription() : ""))
                .orElse(null);
    }
    
    private String getProjectContext(Long projectId) {
        if (projectId == null) return null;
        
        return projectRepository.findById(projectId)
                .map(project -> project.getName() + 
                        (project.getDescription() != null ? " - " + project.getDescription() : ""))
                .orElse(null);
    }
    
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
                Map<String, String> part = new HashMap<>();
                part.put("text", prompt);
                content.put("parts", List.of(part));
                requestBody.put("contents", List.of(content));
                
                // Add generation config for better JSON responses
                Map<String, Object> generationConfig = new HashMap<>();
                generationConfig.put("temperature", 0.3);
                generationConfig.put("maxOutputTokens", 2048);
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
                    log.error("Gemini API unavailable after {} attempts. Service may be temporarily overloaded.", maxRetries);
                    return null;
                }
                
                // Exponential backoff: wait 1s, 2s, 4s
                try {
                    long waitTime = (long) Math.pow(2, attempt - 1) * 1000;
                    log.info("Waiting {}ms before retry...", waitTime);
                    Thread.sleep(waitTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Retry interrupted");
                    return null;
                }
                
            } catch (Exception e) {
                log.error("Error calling Gemini API: {}", e.getMessage(), e);
                return null;
            }
        }
        
        return null;
    }
    
    private FileAnalysisResponse parseGeminiResponse(String response, FileAnalysisRequest request) {
        try {
            // Handle null response (e.g., from service unavailable)
            if (response == null || response.trim().isEmpty()) {
                log.warn("Received null or empty response from Gemini API");
                return FileAnalysisResponse.createDefault(request.getFileName());
            }
            
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode candidatesNode = rootNode.get("candidates");
            
            if (candidatesNode != null && candidatesNode.isArray() && candidatesNode.size() > 0) {
                JsonNode contentNode = candidatesNode.get(0).get("content");
                if (contentNode != null) {
                    JsonNode partsNode = contentNode.get("parts");
                    if (partsNode != null && partsNode.isArray() && partsNode.size() > 0) {
                        String textContent = partsNode.get(0).get("text").asText();
                        
                        // Try to extract JSON from the response
                        return parseAnalysisFromText(textContent, request);
                    }
                }
            }
            
            log.warn("Could not parse Gemini response structure");
            return FileAnalysisResponse.createDefault(request.getFileName());
            
        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", e.getMessage(), e);
            return FileAnalysisResponse.createDefault(request.getFileName());
        }
    }
    
    private FileAnalysisResponse parseAnalysisFromText(String text, FileAnalysisRequest request) {
        try {
            // Find JSON in the response text
            int jsonStart = text.indexOf("{");
            int jsonEnd = text.lastIndexOf("}") + 1;
            
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                String jsonStr = text.substring(jsonStart, jsonEnd);
                JsonNode analysisNode = objectMapper.readTree(jsonStr);
                
                FileAnalysisResponse analysis = new FileAnalysisResponse();
                analysis.setSummary(getTextValue(analysisNode, "summary", "Analysis summary not available"));
                analysis.setCategory(getTextValue(analysisNode, "category", "General"));
                analysis.setTags(getArrayValue(analysisNode, "tags"));
                analysis.setSuggestedDepartment(getTextValue(analysisNode, "suggestedDepartment", null));
                analysis.setSuggestedProject(getTextValue(analysisNode, "suggestedProject", null));
                analysis.setContentType(request.getContentType());
                analysis.setLanguage(getTextValue(analysisNode, "language", "Unknown"));
                analysis.setConfidenceScore(getDoubleValue(analysisNode, "confidenceScore", 0.5));
                analysis.setKeyTopics(getArrayValue(analysisNode, "keyTopics"));
                analysis.setPriority(getTextValue(analysisNode, "priority", "Medium"));
                analysis.setRelatedFiles(getArrayValue(analysisNode, "relatedFiles"));
                analysis.setStorageRecommendation(getTextValue(analysisNode, "storageRecommendation", null));
                analysis.setAccessRecommendation(getTextValue(analysisNode, "accessRecommendation", null));
                
                // Enhanced fields
                analysis.setSuggestedTitle(getTextValue(analysisNode, "suggestedTitle", null));
                analysis.setSuggestedDescription(getTextValue(analysisNode, "suggestedDescription", null));
                analysis.setSuggestedFileTypeName(getTextValue(analysisNode, "suggestedFileTypeName", null));
                analysis.setSuggestedDepartmentCategoryName(getTextValue(analysisNode, "suggestedDepartmentCategoryName", null));
                analysis.setSuggestedVisibility(getTextValue(analysisNode, "suggestedVisibility", "PRIVATE"));
                analysis.setSuggestedPriority(getTextValue(analysisNode, "suggestedPriority", "MEDIUM"));
                
                // Content analysis
                JsonNode contentAnalysisNode = analysisNode.get("contentAnalysis");
                if (contentAnalysisNode != null) {
                    FileAnalysisResponse.ContentAnalysis contentAnalysis = new FileAnalysisResponse.ContentAnalysis();
                    contentAnalysis.setDocumentType(getTextValue(contentAnalysisNode, "documentType", "document"));
                    contentAnalysis.setTechnicalLevel(getTextValue(contentAnalysisNode, "technicalLevel", "BASIC"));
                    contentAnalysis.setEstimatedImportance(getTextValue(contentAnalysisNode, "estimatedImportance", "MEDIUM"));
                    contentAnalysis.setSuggestedAccess(getArrayValue(contentAnalysisNode, "suggestedAccess"));
                    contentAnalysis.setRelatedKeywords(getArrayValue(contentAnalysisNode, "relatedKeywords"));
                    analysis.setContentAnalysis(contentAnalysis);
                }
                
                // Map suggested names to IDs
                mapSuggestedIdsFromNames(analysis);
                
                return analysis;
            }
            
            // Fallback: create analysis from text content
            return createAnalysisFromText(text, request);
            
        } catch (Exception e) {
            log.error("Error parsing analysis from text: {}", e.getMessage());
            return FileAnalysisResponse.createDefault(request.getFileName());
        }
    }
    
    private void mapSuggestedIdsFromNames(FileAnalysisResponse analysis) {
        // Map file type name to ID
        if (analysis.getSuggestedFileTypeName() != null) {
            fileTypeRepository.findAll().stream()
                .filter(ft -> ft.getName().equalsIgnoreCase(analysis.getSuggestedFileTypeName()) ||
                             ft.getName().toLowerCase().contains(analysis.getSuggestedFileTypeName().toLowerCase()) ||
                             analysis.getSuggestedFileTypeName().toLowerCase().contains(ft.getName().toLowerCase()))
                .findFirst()
                .ifPresent(ft -> analysis.setSuggestedFileTypeId(ft.getId()));
        }
        
        // Map department category name to ID and automatically map department
        if (analysis.getSuggestedDepartmentCategoryName() != null) {
            departmentCategoryRepository.findAll().stream()
                .filter(dc -> dc.getName().equalsIgnoreCase(analysis.getSuggestedDepartmentCategoryName()) ||
                             dc.getName().toLowerCase().contains(analysis.getSuggestedDepartmentCategoryName().toLowerCase()) ||
                             analysis.getSuggestedDepartmentCategoryName().toLowerCase().contains(dc.getName().toLowerCase()))
                .findFirst()
                .ifPresent(dc -> {
                    analysis.setSuggestedDepartmentCategoryId(dc.getId());
                    // Also suggest the parent department if not already suggested
                    if (analysis.getSuggestedDepartment() == null || analysis.getSuggestedDepartment().isEmpty()) {
                        analysis.setSuggestedDepartment(dc.getDepartment().getName());
                    }
                });
        }
        
        // If we have a department category but no matching department suggestion, 
        // try to find a department that matches the suggested department name
        if (analysis.getSuggestedDepartment() != null && analysis.getSuggestedDepartmentCategoryId() == null) {
            // Try to find a suitable category within the suggested department
            departmentRepository.findByIsActiveTrueOrderByName().stream()
                .filter(dept -> dept.getName().equalsIgnoreCase(analysis.getSuggestedDepartment()) ||
                               dept.getName().toLowerCase().contains(analysis.getSuggestedDepartment().toLowerCase()) ||
                               analysis.getSuggestedDepartment().toLowerCase().contains(dept.getName().toLowerCase()))
                .findFirst()
                .ifPresent(dept -> {
                    // Look for a general or default category in this department
                    departmentCategoryRepository.findAll().stream()
                        .filter(dc -> dc.getDepartment().getId().equals(dept.getId()))
                        .filter(dc -> dc.getName().toLowerCase().contains("general") || 
                                     dc.getName().toLowerCase().contains("default") ||
                                     dc.getName().toLowerCase().contains("document") ||
                                     dc.getName().toLowerCase().contains("file"))
                        .findFirst()
                        .ifPresent(dc -> {
                            analysis.setSuggestedDepartmentCategoryId(dc.getId());
                            analysis.setSuggestedDepartmentCategoryName(dc.getName());
                        });
                });
        }
    }
    
    private FileAnalysisResponse createAnalysisFromText(String text, FileAnalysisRequest request) {
        FileAnalysisResponse analysis = new FileAnalysisResponse();
        
        // Extract basic information from text
        String[] lines = text.split("\n");
        StringBuilder summary = new StringBuilder();
        List<String> tags = new ArrayList<>();
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            if (summary.length() < 200) {
                summary.append(line.trim()).append(" ");
            }
            
            // Extract potential tags from content
            String[] words = line.toLowerCase().split("\\W+");
            for (String word : words) {
                if (word.length() > 3 && !tags.contains(word) && tags.size() < 10) {
                    tags.add(word);
                }
            }
        }
        
        String fileName = request.getFileName().toLowerCase();
        String contentType = request.getContentType();
        
        analysis.setSummary(summary.length() > 0 ? summary.toString().trim() : "AI analysis completed");
        analysis.setCategory(determineCategory(fileName, contentType));
        analysis.setTags(tags.isEmpty() ? List.of("document") : tags);
        analysis.setContentType(contentType);
        analysis.setLanguage("Unknown");
        analysis.setConfidenceScore(0.7);
        analysis.setKeyTopics(tags.stream().limit(5).collect(Collectors.toList()));
        analysis.setPriority("Medium");
        analysis.setRelatedFiles(new ArrayList<>());
        
        // Enhanced suggestions based on filename and content type
        analysis.setSuggestedTitle(generateTitleFromFilename(request.getFileName()));
        analysis.setSuggestedDescription("Document analyzed by AI system");
        analysis.setSuggestedVisibility(determineVisibilityFromContent(fileName, contentType));
        analysis.setSuggestedPriority(determinePriorityFromContent(fileName, contentType));
        
        // Basic content analysis
        FileAnalysisResponse.ContentAnalysis contentAnalysis = new FileAnalysisResponse.ContentAnalysis();
        contentAnalysis.setDocumentType(determineDocumentType(fileName, contentType));
        contentAnalysis.setTechnicalLevel("BASIC");
        contentAnalysis.setEstimatedImportance("MEDIUM");
        contentAnalysis.setSuggestedAccess(List.of("team", "department"));
        contentAnalysis.setRelatedKeywords(tags.stream().limit(5).collect(Collectors.toList()));
        analysis.setContentAnalysis(contentAnalysis);
        
        // Try to suggest file type based on content and filename
        suggestFileTypeFromContent(analysis, fileName, contentType);
        
        return analysis;
    }
    
    private String generateTitleFromFilename(String fileName) {
        // Remove extension and clean up the filename
        String title = fileName.replaceFirst("\\.[^.]+$", "");
        title = title.replaceAll("[_-]", " ");
        
        // Capitalize first letter of each word
        String[] words = title.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase())
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
    
    private String determineVisibilityFromContent(String fileName, String contentType) {
        fileName = fileName.toLowerCase();
        
        if (fileName.contains("confidential") || fileName.contains("private") || fileName.contains("personal")) {
            return "PRIVATE";
        } else if (fileName.contains("public") || fileName.contains("announcement") || fileName.contains("newsletter")) {
            return "PUBLIC";
        } else {
            return "DEPARTMENT";
        }
    }
    
    private String determinePriorityFromContent(String fileName, String contentType) {
        fileName = fileName.toLowerCase();
        
        if (fileName.contains("urgent") || fileName.contains("critical") || fileName.contains("important") || 
            fileName.contains("emergency") || fileName.contains("deadline")) {
            return "HIGH";
        } else if (fileName.contains("draft") || fileName.contains("temp") || fileName.contains("backup") ||
                  fileName.contains("archive")) {
            return "LOW";
        } else {
            return "MEDIUM";
        }
    }
    
    private String determineDocumentType(String fileName, String contentType) {
        fileName = fileName.toLowerCase();
        
        if (fileName.contains("meeting") || fileName.contains("agenda") || fileName.contains("minutes")) {
            return "meeting";
        } else if (fileName.contains("report") || fileName.contains("analysis") || fileName.contains("summary")) {
            return "report";
        } else if (fileName.contains("proposal") || fileName.contains("plan") || fileName.contains("strategy")) {
            return "planning";
        } else if (fileName.contains("manual") || fileName.contains("guide") || fileName.contains("instruction")) {
            return "documentation";
        } else if (fileName.contains("policy") || fileName.contains("procedure") || fileName.contains("rule")) {
            return "policy";
        } else if (fileName.contains("invoice") || fileName.contains("budget") || fileName.contains("financial")) {
            return "financial";
        } else {
            return "document";
        }
    }
    
    private void suggestFileTypeFromContent(FileAnalysisResponse analysis, String fileName, String contentType) {
        String category = analysis.getCategory().toLowerCase();
        String docType = analysis.getContentAnalysis().getDocumentType();
        
        // Try to find the best matching file type
        List<FileType> fileTypes = fileTypeRepository.findAll();
        
        for (FileType fileType : fileTypes) {
            String ftName = fileType.getName().toLowerCase();
            if (ftName.contains(category) || ftName.contains(docType) || 
                category.contains(ftName) || docType.contains(ftName)) {
                analysis.setSuggestedFileTypeId(fileType.getId());
                analysis.setSuggestedFileTypeName(fileType.getName());
                break;
            }
        }
        
        // Fallback: suggest based on common patterns
        if (analysis.getSuggestedFileTypeId() == null) {
            for (FileType fileType : fileTypes) {
                String ftName = fileType.getName().toLowerCase();
                if ((ftName.contains("document") && contentType.contains("text")) ||
                    (ftName.contains("presentation") && contentType.contains("presentation")) ||
                    (ftName.contains("spreadsheet") && contentType.contains("spreadsheet")) ||
                    (ftName.contains("image") && contentType.contains("image")) ||
                    (ftName.contains("pdf") && contentType.contains("pdf"))) {
                    analysis.setSuggestedFileTypeId(fileType.getId());
                    analysis.setSuggestedFileTypeName(fileType.getName());
                    break;
                }
            }
        }
    }
    
    private String determineCategory(String fileName, String contentType) {
        String lowerFileName = fileName.toLowerCase();
        
        if (lowerFileName.contains("report")) return "Report";
        if (lowerFileName.contains("presentation") || contentType.contains("presentation")) return "Presentation";
        if (lowerFileName.contains("contract") || lowerFileName.contains("agreement")) return "Legal";
        if (lowerFileName.contains("invoice") || lowerFileName.contains("receipt")) return "Financial";
        if (lowerFileName.contains("plan") || lowerFileName.contains("strategy")) return "Planning";
        if (contentType.contains("image")) return "Image";
        if (contentType.contains("video")) return "Video";
        if (contentType.contains("audio")) return "Audio";
        
        return "Document";
    }
    
    private String getTextValue(JsonNode node, String fieldName, String defaultValue) {
        JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() ? field.asText() : defaultValue;
    }
    
    private Double getDoubleValue(JsonNode node, String fieldName, Double defaultValue) {
        JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() ? field.asDouble() : defaultValue;
    }
    
    private List<String> getArrayValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        List<String> result = new ArrayList<>();
        
        if (field != null && field.isArray()) {
            field.forEach(item -> result.add(item.asText()));
        }
        
        return result;
    }
    
    public boolean canAnalyzeFile(String fileName, long fileSize, String contentType) {
        if (!geminiProperties.isEnabled()) {
            return false;
        }
        
        // Always allow analysis - large files will be analyzed using metadata only
        String extension = getFileExtension(fileName);
        boolean isSupportedFormat = geminiProperties.getSupportedFormatsList().contains(extension.toLowerCase());
        
        // For unsupported formats, still allow metadata-only analysis if it's a common file type
        if (!isSupportedFormat) {
            // Allow analysis for common office documents, images, etc. based on content type
            return contentType != null && (
                contentType.contains("application/") ||
                contentType.contains("text/") ||
                contentType.contains("image/") ||
                contentType.contains("video/") ||
                contentType.contains("audio/")
            );
        }
        
        return true;
    }
    
    public AnalysisCapability getAnalysisCapability(String fileName, long fileSize, String contentType) {
        if (!canAnalyzeFile(fileName, fileSize, contentType)) {
            return AnalysisCapability.NOT_SUPPORTED;
        }
        
        if (fileSize > geminiProperties.getMaxFileSize()) {
            return AnalysisCapability.METADATA_ONLY;
        }
        
        String extension = getFileExtension(fileName);
        boolean isSupportedForContent = geminiProperties.getSupportedFormatsList().contains(extension.toLowerCase());
        
        return isSupportedForContent ? AnalysisCapability.FULL_CONTENT : AnalysisCapability.METADATA_ONLY;
    }
    
    public enum AnalysisCapability {
        NOT_SUPPORTED,
        METADATA_ONLY,
        FULL_CONTENT
    }
    
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "";
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
