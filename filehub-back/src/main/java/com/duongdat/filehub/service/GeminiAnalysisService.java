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
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
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
    private final FileContentExtractorService fileContentExtractorService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public GeminiAnalysisService(GeminiProperties geminiProperties,
                                DepartmentRepository departmentRepository,
                                ProjectRepository projectRepository,
                                FileTypeRepository fileTypeRepository,
                                DepartmentCategoryRepository departmentCategoryRepository,
                                FileContentExtractorService fileContentExtractorService,
                                RestTemplateBuilder restTemplateBuilder) {
        this.geminiProperties = geminiProperties;
        this.departmentRepository = departmentRepository;
        this.projectRepository = projectRepository;
        this.fileTypeRepository = fileTypeRepository;
        this.departmentCategoryRepository = departmentCategoryRepository;
        this.fileContentExtractorService = fileContentExtractorService;
        // Configure RestTemplate with timeouts for AI API calls
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(120)) // 2 minutes for AI processing
                .build();
    }
    
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent";
    private static final String GEMINI_FILES_API_URL = "https://generativelanguage.googleapis.com/v1beta/files";
    
    public FileAnalysisResponse analyzeFile(FileAnalysisRequest request) {
        if (!geminiProperties.isEnabled()) {
            log.info("Gemini AI is disabled, returning default analysis");
            return FileAnalysisResponse.createDefault(request.getFileName());
        }

        try {
            String prompt = buildAnalysisPrompt(request);
            
            // Check if file is DOCX or DOC and extract text content
            String extractedTextContent = null;
            byte[] fileDataToUpload = null;
            boolean useFileUpload = false;
            boolean useTextContent = false;
            
            if (request.getFileData() != null) {
                // Check if it's a DOCX or DOC file that we should convert to text
                String fileName = request.getFileName().toLowerCase();
                boolean isWordDocument = fileName.endsWith(".docx") || fileName.endsWith(".doc");
                
                if (isWordDocument) {
                    try {
                        // Extract text content directly from byte array
                        extractedTextContent = extractTextFromWordDocument(
                            request.getFileData(), 
                            request.getFileName(),
                            request.getContentType()
                        );
                        
                        if (extractedTextContent != null && !extractedTextContent.trim().isEmpty()) {
                            useTextContent = true;
                            log.info("Extracted text content from {} ({} characters)", 
                                request.getFileName(), extractedTextContent.length());
                        } else {
                            log.warn("Failed to extract text content from {}, falling back to binary file upload", 
                                request.getFileName());
                        }
                    } catch (Exception e) {
                        log.error("Error extracting text from {}: {}", request.getFileName(), e.getMessage());
                        log.info("Falling back to binary file upload for {}", request.getFileName());
                    }
                }
                
                // If text extraction failed or not a Word document, use original approach
                if (!useTextContent) {
                    if (isMimeTypeSupported(request.getContentType())) {
                        fileDataToUpload = request.getFileData();
                        useFileUpload = true;
                        log.info("Using Gemini Files API for file analysis: {} (MIME: {})", 
                            request.getFileName(), request.getContentType());
                    } else {
                        log.info("File '{}' with MIME type '{}' not supported for content analysis. Using metadata-only analysis.", 
                            request.getFileName(), request.getContentType());
                    }
                }
            } else {
                log.info("No file data provided for '{}'. Using metadata-only analysis.", request.getFileName());
            }
            
            String geminiResponse;
            if (useTextContent) {
                // Send text content instead of binary file
                geminiResponse = callGeminiAPIWithTextContent(prompt, extractedTextContent);
                log.info("Analysis completed for '{}' using text content extraction", request.getFileName());
            } else {
                // Use original approach with binary file upload or metadata-only
                geminiResponse = callGeminiAPI(prompt, fileDataToUpload, request.getContentType());
            }
            
            FileAnalysisResponse response = parseGeminiResponse(geminiResponse, request);
            
            // Add analysis method indicator and log the method used
            if (response.getContentAnalysis() != null) {
                String analysisMethod;
                if (useTextContent) {
                    analysisMethod = "text_content_analysis";
                } else if (useFileUpload) {
                    analysisMethod = "files_api_analysis";
                } else {
                    analysisMethod = "metadata_only";
                }
                response.getContentAnalysis().setAnalysisMethod(analysisMethod);
                log.info("Analysis completed for '{}' using method: {}", request.getFileName(), analysisMethod);
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("Error analyzing file with Gemini AI: {}", e.getMessage(), e);
            return FileAnalysisResponse.createDefault(request.getFileName());
        }
    }    private String buildAnalysisPrompt(FileAnalysisRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        // Get department and project context
        String departmentContext = getDepartmentContext(request.getDepartmentId());
        String projectContext = getProjectContext(request.getProjectId());
        
        // Determine analysis method based on file data availability and MIME type support
        boolean useFileUpload = request.getFileData() != null && isMimeTypeSupported(request.getContentType());
        
        if (useFileUpload) {
            prompt.append("IMPORTANT: I have uploaded a file for you to analyze. You MUST read and analyze the ACTUAL CONTENT of the uploaded file, not just the filename or metadata.\n\n");
            prompt.append("Analyze the following file using the uploaded file content. Please read the file content and provide analysis based on what you actually see/read inside the file:\n\n");
        } else if (request.getFileData() != null) {
            prompt.append("IMPORTANT: This file type (MIME: ").append(request.getContentType()).append(") is not supported for content analysis by Gemini API.\n");
            prompt.append("Analyzing based on available metadata only (filename, file type, size, description):\n\n");
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
        
        // Note about file analysis approach
        if (useFileUpload) {
            prompt.append("\nIMPORTANT: A file has been uploaded to Gemini Files API for analysis. Please analyze the actual content of the uploaded file, not just the metadata.\n");
            prompt.append("Read and analyze the file content thoroughly to provide accurate insights based on what you actually see in the file.\n");
        } else if (request.getFileData() != null) {
            prompt.append("\nNOTE: File content analysis is not available for this file type. Analysis is based on metadata only.\n");
            prompt.append("The MIME type '").append(request.getContentType()).append("' is not supported by Gemini API for content analysis.\n");
            prompt.append("Supported formats include: images (jpg, png, gif, webp), videos (mp4, mov, avi, etc.), audio (wav, mp3, etc.), text files, and PDFs.\n");
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
        prompt.append("    \"analysisMethod\": \"").append(useFileUpload ? "files_api_analysis" : "metadata_only").append("\"\n");
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
        if (useFileUpload) {
            prompt.append("- CRITICAL: This file has been uploaded via Gemini Files API - you MUST analyze the ACTUAL file content\n");
            prompt.append("- DO NOT just analyze the filename or metadata - READ THE ACTUAL FILE CONTENT\n");
            prompt.append("- Read the file thoroughly and base your analysis on the actual content you see inside the file\n");
            prompt.append("- For documents: extract and analyze the actual text, structure, and meaning from the document\n");
            prompt.append("- For images: describe exactly what you see in the image, identify objects, text, scenes, or diagrams\n");
            prompt.append("- For videos: analyze the visual content, scenes, actions, or any text visible in the video\n");
            prompt.append("- For audio: analyze speech content, music, or other audio characteristics you hear\n");
            prompt.append("- Your analysis MUST reflect the ACTUAL content of the uploaded file, not just the filename\n");
            prompt.append("- Provide specific insights based on what you actually read/see/hear in the file\n");
            prompt.append("- Use high confidence scores when you can clearly read/see the file content\n");
            prompt.append("- If you cannot access the file content, explicitly state this in your response\n");
        } else if (request.getFileData() != null) {
            prompt.append("- IMPORTANT: File content analysis is NOT available for this file type (").append(request.getContentType()).append(")\n");
            prompt.append("- Analysis is based ONLY on metadata: filename, file type, size, and provided description\n");
            prompt.append("- Make reasonable inferences based on filename patterns and file type\n");
            prompt.append("- Use moderate confidence scores (0.3-0.6) since analysis is metadata-only\n");
            prompt.append("- Clearly indicate in summary that analysis is based on metadata only\n");
        } else {
            prompt.append("- Base analysis on available metadata (filename, title, description)\n");
            prompt.append("- Make reasonable inferences about content based on file type\n");
        }
        
        prompt.append("- When suggesting a department category, make sure it belongs to the suggested department\n");
        prompt.append("- For visibility: PRIVATE for sensitive/personal content, SHARED for team sharing, PUBLIC for general access\n");
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
    
    private String callGeminiAPI(String prompt, byte[] fileData, String contentType) {
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
                
                // Always upload file to Files API if file data is available
                if (fileData != null) {
                    try {
                        String fileUri = uploadFileToGeminiAPI(fileData, contentType);
                        if (fileUri != null) {
                            // Add file part with correct structure for Gemini API
                            Map<String, Object> filePart = new HashMap<>();
                            Map<String, String> fileDataMap = new HashMap<>();
                            fileDataMap.put("file_uri", fileUri);  // Use correct field name
                            filePart.put("file_data", fileDataMap);  // Use correct field name
                            parts.add(filePart);
                            
                            log.info("Added file reference to Gemini request: {}", fileUri);
                            log.debug("File part structure: {}", filePart);
                        } else {
                            log.warn("File upload returned null URI, continuing with metadata-only analysis");
                        }
                    } catch (Exception e) {
                        log.error("Failed to upload file to Gemini Files API: {}", e.getMessage(), e);
                        log.warn("Continuing with metadata-only analysis due to upload failure");
                        
                        // Check if this is a MIME type error
                        if (e.getMessage() != null && e.getMessage().contains("mimeType parameter")) {
                            log.info("MIME type '{}' is not supported by Gemini API. This file type cannot be analyzed for content.", contentType);
                        }
                    }
                }
                
                content.put("parts", parts);
                requestBody.put("contents", List.of(content));
                
                // Add generation config for better JSON responses with increased limits
                Map<String, Object> generationConfig = new HashMap<>();
                generationConfig.put("temperature", geminiProperties.getTemperature());
                generationConfig.put("maxOutputTokens", geminiProperties.getMaxOutputTokens());
                requestBody.put("generationConfig", generationConfig);
                
                // Log request details for debugging
                log.debug("Gemini API request URL: {}", url);
                log.debug("Request body parts count: {}", parts.size());
                log.debug("Has file data: {}", fileData != null);
                if (log.isTraceEnabled()) {
                    log.trace("Full request body: {}", requestBody);
                }
                
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
    
    /**
     * Call Gemini API with text content instead of binary file upload
     */
    private String callGeminiAPIWithTextContent(String prompt, String textContent) {
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
                
                // Add text part with extracted content
                Map<String, Object> textPart = new HashMap<>();
                String enhancedPrompt = prompt + "\n\nFile Content:\n" + textContent;
                textPart.put("text", enhancedPrompt);
                parts.add(textPart);
                
                content.put("parts", parts);
                requestBody.put("contents", List.of(content));
                
                // Add generation config
                Map<String, Object> generationConfig = new HashMap<>();
                generationConfig.put("temperature", geminiProperties.getTemperature());
                generationConfig.put("maxOutputTokens", geminiProperties.getMaxOutputTokens());
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
                log.error("Error calling Gemini API with text content: {}", e.getMessage(), e);
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * Extract text content from Word documents (DOC/DOCX)
     */
    private String extractTextFromWordDocument(byte[] fileData, String fileName, String contentType) {
        try {
            // Create a simple MultipartFile implementation for FileContentExtractorService
            SimpleMultipartFile tempFile = new SimpleMultipartFile(fileName, fileData, contentType);
            return fileContentExtractorService.extractTextContent(tempFile);
        } catch (Exception e) {
            log.error("Error extracting text from Word document: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Simple MultipartFile implementation for internal use
     */
    private static class SimpleMultipartFile implements MultipartFile {
        private final String name;
        private final byte[] content;
        private final String contentType;
        
        public SimpleMultipartFile(String name, byte[] content, String contentType) {
            this.name = name;
            this.content = content;
            this.contentType = contentType;
        }
        
        @Override
        public String getName() { return "file"; }
        
        @Override
        public String getOriginalFilename() { return name; }
        
        @Override
        public String getContentType() { return contentType; }
        
        @Override
        public boolean isEmpty() { return content.length == 0; }
        
        @Override
        public long getSize() { return content.length; }
        
        @Override
        public byte[] getBytes() { return content; }
        
        @Override
        public java.io.InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }
        
        @Override
        public void transferTo(java.io.File dest) throws java.io.IOException {
            throw new UnsupportedOperationException("transferTo not supported");
        }
    }
    
    /**
     * Upload file to Gemini Files API using the correct media upload approach
     */
    private String uploadFileToGeminiAPI(byte[] fileData, String contentType) {
        try {
            // Check if the content type is supported by Gemini
            if (!isMimeTypeSupported(contentType)) {
                log.warn("MIME type '{}' is not supported by Gemini API. Skipping file upload for content analysis.", contentType);
                return null;
            }
            
            // Use the correct media upload URL for Gemini Files API
            String mediaUploadUrl = "https://generativelanguage.googleapis.com/upload/v1beta/files?uploadType=media&key=" + 
                                   geminiProperties.getApi().getKey();
            
            HttpHeaders headers = new HttpHeaders();
            // Use the provided content type, with fallback to octet-stream if null
            String mimeType = (contentType != null && !contentType.isEmpty()) ? contentType : "application/octet-stream";
            headers.set("Content-Type", mimeType);
            headers.set("X-Goog-Upload-Protocol", "raw");
            
            HttpEntity<byte[]> entity = new HttpEntity<>(fileData, headers);
            
            log.debug("Uploading {} bytes to Gemini Files API using media upload with MIME type: {}", fileData.length, mimeType);
            log.info("Attempting file upload with supported MIME type: {}", mimeType);
            
            ResponseEntity<String> response = restTemplate.exchange(
                mediaUploadUrl, HttpMethod.POST, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("Media upload response: {}", response.getBody());
                
                try {
                    JsonNode responseNode = objectMapper.readTree(response.getBody());
                    // Check if response has "file" object structure (media upload response)
                    JsonNode fileNode = responseNode.has("file") ? responseNode.get("file") : responseNode;
                    String fileUri = fileNode.get("uri").asText();
                    String displayName = fileNode.has("displayName") ? 
                                       fileNode.get("displayName").asText() : "uploaded_file";
                    String state = fileNode.has("state") ? 
                                 fileNode.get("state").asText() : "ACTIVE";
                    
                    log.info("File uploaded successfully via media upload. URI: {}, Name: {}, State: {}", 
                        fileUri, displayName, state);
                    
                    // Wait for processing if needed
                    if ("PROCESSING".equals(state)) {
                        log.info("File is processing, waiting...");
                        try {
                            for (int i = 0; i < 10; i++) { // Max 30 seconds
                                Thread.sleep(3000);
                                String currentState = checkFileStatus(fileUri);
                                if ("ACTIVE".equals(currentState)) {
                                    log.info("File processing completed");
                                    break;
                                }
                                log.debug("Still processing... attempt {}", i + 1);
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    
                    return fileUri;
                    
                } catch (Exception e) {
                    log.error("Error parsing media upload response: {} - Response: {}", 
                        e.getMessage(), response.getBody());
                }
            } else {
                log.error("Media upload failed. Status: {}, Body: {}", 
                    response.getStatusCode(), response.getBody());
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("Error in media upload for MIME type '{}': {}", contentType, e.getMessage(), e);
            
            // Check if the error is specifically about unsupported MIME type
            if (e.getMessage() != null && e.getMessage().contains("mimeType parameter")) {
                log.warn("MIME type '{}' is not supported by Gemini API. Falling back to metadata-only analysis.", contentType);
            }
            
            return null;
        }
    }
    
    /**
     * Check if a MIME type is supported by Gemini API for file uploads
     */
    private boolean isMimeTypeSupported(String contentType) {
        if (contentType == null || contentType.isEmpty()) {
            return false;
        }
        
        // List of supported MIME types based on Gemini documentation
        Set<String> supportedMimeTypes = Set.of(
            // Images
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp",
            
            // Audio
            "audio/wav", "audio/mp3", "audio/aiff", "audio/aac", "audio/ogg", "audio/flac",
            
            // Video
            "video/mp4", "video/mpeg", "video/mov", "video/avi", "video/x-flv", 
            "video/mpg", "video/webm", "video/wmv", "video/3gpp",
            
            // Text
            "text/plain", "text/html", "text/css", "text/javascript", "text/markdown",
            
            // Documents (limited support)
            "application/pdf",
            
            // Code files
            "application/x-javascript", "application/json"
        );
        
        // Check exact match first
        if (supportedMimeTypes.contains(contentType.toLowerCase())) {
            log.debug("MIME type '{}' is explicitly supported", contentType);
            return true;
        }
        
        // Check by category for broader support
        String lowerContentType = contentType.toLowerCase();
        if (lowerContentType.startsWith("image/") || 
            lowerContentType.startsWith("audio/") || 
            lowerContentType.startsWith("video/") ||
            lowerContentType.startsWith("text/")) {
            
            log.debug("MIME type '{}' is supported by category", contentType);
            return true;
        }
        
        // Special handling for application types that might be supported
        if (lowerContentType.equals("application/pdf")) {
            log.debug("MIME type '{}' (PDF) is supported", contentType);
            return true;
        }
        
        log.debug("MIME type '{}' is not supported by Gemini API", contentType);
        return false;
    }
    
    private String checkFileStatus(String fileUri) {
        try {
            String fileUrl = "https://generativelanguage.googleapis.com/v1beta/files/" + 
                           fileUri.substring(fileUri.lastIndexOf("/") + 1) + "?key=" + geminiProperties.getApi().getKey();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                fileUrl, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                try {
                    JsonNode responseNode = objectMapper.readTree(response.getBody());
                    // Handle both direct response and wrapped response structures
                    JsonNode fileNode = responseNode.has("file") ? responseNode.get("file") : responseNode;
                    String state = fileNode.has("state") ? fileNode.get("state").asText() : "unknown";
                    log.debug("File status check - URI: {}, State: {}", fileUri, state);
                    return state;
                } catch (Exception e) {
                    log.error("Error parsing file status response: {}", e.getMessage());
                    return "unknown";
                }
            } else {
                log.error("Failed to check file status. Status: {}, Body: {}", 
                    response.getStatusCode(), response.getBody());
                return "error";
            }
        } catch (Exception e) {
            log.error("Error checking file status: {}", e.getMessage(), e);
            return "error";
        }
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
        
        // Set analysis method based on whether we had file data
        boolean hadFileData = request.getFileData() != null;
        boolean supportedMimeType = hadFileData && isMimeTypeSupported(request.getContentType());
        contentAnalysis.setAnalysisMethod(supportedMimeType ? "files_api_analysis" : "metadata_only");
        
        analysis.setContentAnalysis(contentAnalysis);
        
        // Adjust confidence score and summary based on analysis method
        if (!supportedMimeType && hadFileData) {
            analysis.setConfidenceScore(0.4); // Lower confidence for metadata-only analysis
            analysis.setSummary("Metadata-only analysis - file type not supported for content analysis. " + analysis.getSummary());
        } else if (!hadFileData) {
            analysis.setConfidenceScore(0.5); // Moderate confidence for no file data
        }
        
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
        
        // Check if MIME type is supported for content analysis
        if (isMimeTypeSupported(contentType)) {
            log.debug("File '{}' with MIME type '{}' supports full content analysis", fileName, contentType);
            return AnalysisCapability.FULL_CONTENT;
        } else {
            log.debug("File '{}' with MIME type '{}' supports metadata-only analysis", fileName, contentType);
            return AnalysisCapability.METADATA_ONLY;
        }
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
