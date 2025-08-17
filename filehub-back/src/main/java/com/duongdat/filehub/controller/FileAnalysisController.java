package com.duongdat.filehub.controller;

import com.duongdat.filehub.config.GeminiProperties;
import com.duongdat.filehub.dto.request.FileAnalysisRequest;
import com.duongdat.filehub.dto.response.ApiResponse;
import com.duongdat.filehub.dto.response.FileAnalysisResponse;
import com.duongdat.filehub.service.FileContentExtractorService;
import com.duongdat.filehub.service.GeminiAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai/analysis")
@RequiredArgsConstructor
@Slf4j
public class FileAnalysisController {
    
    private final GeminiAnalysisService geminiAnalysisService;
    private final FileContentExtractorService fileContentExtractorService;
    private final GeminiProperties geminiProperties;
    
    @PostMapping("/file")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FileAnalysisResponse>> analyzeFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "projectId", required = false) Long projectId,
            @RequestParam(value = "description", required = false) String description) {
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("File is empty"));
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid file name"));
            }
            
            // Extract content for small, supported files
            String fileContent = "";
            if (geminiAnalysisService.canAnalyzeFile(fileName, file.getSize(), file.getContentType()) &&
                fileContentExtractorService.isContentExtractable(fileName)) {
                
                fileContent = fileContentExtractorService.extractTextContent(file);
                log.debug("Extracted {} characters of content from file: {}", fileContent.length(), fileName);
            }
            
            // Create analysis request
            FileAnalysisRequest request = new FileAnalysisRequest();
            request.setFileName(fileName);
            request.setFileContent(fileContent);
            request.setContentType(file.getContentType());
            request.setDepartmentId(departmentId);
            request.setProjectId(projectId);
            request.setDescription(description);
            
            // Analyze with Gemini AI
            FileAnalysisResponse analysis = geminiAnalysisService.analyzeFile(request);
            
            return ResponseEntity.ok(ApiResponse.success("File analyzed successfully", analysis));
            
        } catch (Exception e) {
            log.error("Error analyzing file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to analyze file: " + e.getMessage()));
        }
    }
    
    @PostMapping("/text")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FileAnalysisResponse>> analyzeText(
            @RequestBody FileAnalysisRequest request) {
        
        try {
            if (request.getFileName() == null || request.getFileName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("File name is required"));
            }
            
            // Analyze with Gemini AI
            FileAnalysisResponse analysis = geminiAnalysisService.analyzeFile(request);
            
            return ResponseEntity.ok(ApiResponse.success("Text analyzed successfully", analysis));
            
        } catch (Exception e) {
            log.error("Error analyzing text: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to analyze text: " + e.getMessage()));
        }
    }
    
    @GetMapping("/capabilities")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getAnalysisCapabilities() {
        try {
            var capabilities = java.util.Map.of(
                "geminiEnabled", geminiProperties.isEnabled(),
                "maxFileSize", geminiProperties.getMaxFileSize(),
                "supportedFormats", geminiProperties.getSupportedFormats(),
                "supportedFormatsList", geminiProperties.getSupportedFormatsList(),
                "modelName", geminiProperties.getModel().getName(),
                "features", java.util.List.of(
                    "Content summarization",
                    "Category detection", 
                    "Tag generation",
                    "Department suggestion",
                    "Project suggestion",
                    "Language detection",
                    "Priority assessment",
                    "Storage recommendations"
                )
            );
            
            return ResponseEntity.ok(ApiResponse.success("Analysis capabilities retrieved", capabilities));
            
        } catch (Exception e) {
            log.error("Error getting analysis capabilities: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get capabilities"));
        }
    }
}
