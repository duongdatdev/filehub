package com.duongdat.filehub.controller;

import com.duongdat.filehub.dto.request.FileAnalysisRequest;
import com.duongdat.filehub.dto.request.FileUploadRequest;
import com.duongdat.filehub.dto.response.ApiResponse;
import com.duongdat.filehub.dto.response.FileAnalysisResponse;
import com.duongdat.filehub.dto.response.FileResponse;
import com.duongdat.filehub.dto.response.FileUploadWithAnalysisResponse;
import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.service.FileContentExtractorService;
import com.duongdat.filehub.service.FileService;
import com.duongdat.filehub.service.GeminiAnalysisService;
import com.duongdat.filehub.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    
    private final FileService fileService;
    private final SecurityUtil securityUtil;
    private final GeminiAnalysisService geminiAnalysisService;
    private final FileContentExtractorService fileContentExtractorService;
    
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FileUploadWithAnalysisResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "departmentCategoryId", required = false) Long departmentCategoryId,
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "projectId", required = false) Long projectId,
            @RequestParam(value = "fileTypeId", required = false) Long fileTypeId,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "visibility", defaultValue = "PRIVATE") String visibility,
            @RequestParam(value = "enableAiAnalysis", defaultValue = "true") boolean enableAiAnalysis) {
        try {
            log.debug("File upload request received - File: {}, Title: {}, Description: {}, DepartmentCategoryId: {}, DepartmentId: {}, ProjectId: {}, FileTypeId: {}, Tags: {}, Visibility: {}, EnableAiAnalysis: {}", 
                      file != null ? file.getOriginalFilename() : "null", title, description, departmentCategoryId, departmentId, projectId, fileTypeId, tags, visibility, enableAiAnalysis);
            
            // Validate file parameter
            if (file == null || file.isEmpty()) {
                log.warn("File upload failed - File is null or empty");
                return ResponseEntity.badRequest().body(ApiResponse.error("File is required and cannot be empty"));
            }
            
            // Create FileUploadRequest from individual parameters
            FileUploadRequest request = new FileUploadRequest();
            request.setTitle(title != null && !title.trim().isEmpty() ? title.trim() : file.getOriginalFilename());
            request.setDescription(description != null ? description.trim() : null);
            request.setDepartmentCategoryId(departmentCategoryId);
            request.setDepartmentId(departmentId);
            request.setProjectId(projectId);
            request.setFileTypeId(fileTypeId);
            // Handle tags - set to null if empty to avoid JSON parsing issues
            request.setTags(tags != null && !tags.trim().isEmpty() ? tags.trim() : null);
            request.setVisibility(visibility != null ? visibility.toUpperCase() : "PRIVATE");
            
            log.debug("Processing file upload for user with request: {}", request);
            FileResponse fileResponse = fileService.uploadFile(file, request);
            log.info("File uploaded successfully - ID: {}, Filename: {}", fileResponse.getId(), fileResponse.getOriginalFilename());
            
            // Perform AI analysis if enabled
            FileUploadWithAnalysisResponse uploadResponse;
            if (enableAiAnalysis) {
                try {
                    String fileName = file.getOriginalFilename();
                    if (fileName == null) {
                        fileName = "unknown";
                    }
                    
                    // Check if file can be analyzed
                    if (geminiAnalysisService.canAnalyzeFile(fileName, file.getSize(), file.getContentType())) {
                        // Determine analysis capability
                        GeminiAnalysisService.AnalysisCapability capability = 
                            geminiAnalysisService.getAnalysisCapability(fileName, file.getSize(), file.getContentType());
                        
                        // Extract content for small, supported files only
                        String fileContent = "";
                        if (capability == GeminiAnalysisService.AnalysisCapability.FULL_CONTENT && 
                            fileContentExtractorService.isContentExtractable(fileName)) {
                            fileContent = fileContentExtractorService.extractTextContent(file);
                            log.debug("Extracted {} characters of content from file: {}", fileContent.length(), fileName);
                        }
                        
                        // Create analysis request with enhanced metadata
                        FileAnalysisRequest analysisRequest = new FileAnalysisRequest();
                        analysisRequest.setFileName(fileName);
                        analysisRequest.setFileContent(fileContent);
                        analysisRequest.setContentType(file.getContentType());
                        analysisRequest.setDepartmentId(departmentId);
                        analysisRequest.setProjectId(projectId);
                        analysisRequest.setDescription(description);
                        analysisRequest.setFileSize(file.getSize());
                        analysisRequest.setTitle(title); // Use the title from upload request
                        
                        // Analyze with Gemini AI
                        FileAnalysisResponse analysisResponse = geminiAnalysisService.analyzeFile(analysisRequest);
                        uploadResponse = FileUploadWithAnalysisResponse.withAnalysis(fileResponse, analysisResponse);
                        
                        if (capability == GeminiAnalysisService.AnalysisCapability.METADATA_ONLY) {
                            log.info("AI analysis completed for large file using metadata only: {} ({})", 
                                fileName, formatBytes(file.getSize()));
                        } else {
                            log.info("AI analysis completed for file with content: {}", fileName);
                        }
                    } else {
                        uploadResponse = FileUploadWithAnalysisResponse.withoutAnalysis(fileResponse, 
                                "File type not supported for AI analysis");
                        log.debug("AI analysis skipped for file: {} - unsupported type", fileName);
                    }
                } catch (Exception aiException) {
                    log.warn("AI analysis failed for file: {} - {}", file.getOriginalFilename(), aiException.getMessage());
                    uploadResponse = FileUploadWithAnalysisResponse.withAnalysisError(fileResponse, aiException.getMessage());
                }
            } else {
                uploadResponse = FileUploadWithAnalysisResponse.withoutAnalysis(fileResponse, "AI analysis disabled by user");
                log.debug("AI analysis disabled for file: {}", file.getOriginalFilename());
            }
            
            return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", uploadResponse));
        } catch (IOException e) {
            log.error("File upload failed due to IOException: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to upload file: " + e.getMessage()));
        } catch (RuntimeException e) {
            log.error("File upload failed due to RuntimeException: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("File upload failed due to unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Unexpected error occurred: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{fileId}/analyze")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FileAnalysisResponse>> analyzeExistingFile(
            @PathVariable Long fileId,
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "projectId", required = false) Long projectId,
            @RequestParam(value = "description", required = false) String description) {
        try {
            // Get file information
            FileResponse fileResponse = fileService.getFileById(fileId)
                    .orElseThrow(() -> new RuntimeException("File not found"));
            
            // Check if file can be analyzed
            if (!geminiAnalysisService.canAnalyzeFile(fileResponse.getOriginalFilename(), 
                    fileResponse.getFileSize(), fileResponse.getContentType())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("File type not supported for AI analysis"));
            }
            
            // Determine analysis capability
            GeminiAnalysisService.AnalysisCapability capability = 
                geminiAnalysisService.getAnalysisCapability(fileResponse.getOriginalFilename(), 
                    fileResponse.getFileSize(), fileResponse.getContentType());
            
            // Get file content for analysis (only for small files)
            String fileContent = "";
            if (capability == GeminiAnalysisService.AnalysisCapability.FULL_CONTENT && 
                fileContentExtractorService.isContentExtractable(fileResponse.getOriginalFilename())) {
                try {
                    byte[] fileData = fileService.downloadFile(fileId);
                    // Create a temporary multipart file for content extraction
                    fileContent = new String(fileData, "UTF-8");
                    if (fileContent.length() > 50000) { // Limit content size
                        fileContent = fileContent.substring(0, 50000) + "... [truncated]";
                    }
                } catch (Exception e) {
                    log.warn("Failed to extract content from existing file: {} - {}", fileResponse.getOriginalFilename(), e.getMessage());
                }
            }
            
            // Create enhanced analysis request
            FileAnalysisRequest analysisRequest = new FileAnalysisRequest();
            analysisRequest.setFileName(fileResponse.getOriginalFilename());
            analysisRequest.setFileContent(fileContent);
            analysisRequest.setContentType(fileResponse.getContentType());
            analysisRequest.setDepartmentId(departmentId != null ? departmentId : fileResponse.getDepartmentId());
            analysisRequest.setProjectId(projectId != null ? projectId : fileResponse.getProjectId());
            analysisRequest.setDescription(description != null ? description : fileResponse.getDescription());
            analysisRequest.setFileSize(fileResponse.getFileSize());
            analysisRequest.setTitle(fileResponse.getTitle());
            
            // Analyze with Gemini AI
            FileAnalysisResponse analysisResponse = geminiAnalysisService.analyzeFile(analysisRequest);
            
            if (capability == GeminiAnalysisService.AnalysisCapability.METADATA_ONLY) {
                log.info("AI analysis completed for large existing file using metadata only: {} (ID: {}, {})", 
                    fileResponse.getOriginalFilename(), fileId, formatBytes(fileResponse.getFileSize()));
            } else {
                log.info("AI analysis completed for existing file with content: {} (ID: {})", 
                    fileResponse.getOriginalFilename(), fileId);
            }
            
            return ResponseEntity.ok(ApiResponse.success("File analyzed successfully", analysisResponse));
            
        } catch (Exception e) {
            log.error("Error analyzing existing file {}: {}", fileId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to analyze file: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<FileResponse>>> getUserFiles(
            @RequestParam(required = false) String filename,
            @RequestParam(required = false) Long departmentCategoryId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long fileTypeId,
            @RequestParam(required = false) String contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            Long userId = securityUtil.getCurrentUserId()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            PageResponse<FileResponse> files = fileService.getUserFiles(
                    userId, filename, departmentCategoryId, departmentId, projectId, fileTypeId, contentType, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(ApiResponse.success("Files retrieved successfully", files));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<FileResponse>>> getAllFilesWithFilters(
            @RequestParam(required = false) String filename,
            @RequestParam(required = false) Long departmentCategoryId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long fileTypeId,
            @RequestParam(required = false) String contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            PageResponse<FileResponse> files = fileService.getAllFilesWithFilters(
                    filename, departmentCategoryId, departmentId, projectId, userId, fileTypeId, contentType, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(ApiResponse.success("Files retrieved successfully", files));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<java.util.List<FileResponse>>> getFilesByDepartment(@PathVariable Long departmentId) {
        try {
            java.util.List<FileResponse> files = fileService.getFilesByDepartment(departmentId);
            return ResponseEntity.ok(ApiResponse.success("Files retrieved successfully", files));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<java.util.List<FileResponse>>> getFilesByProject(@PathVariable Long projectId) {
        try {
            java.util.List<FileResponse> files = fileService.getFilesByProject(projectId);
            return ResponseEntity.ok(ApiResponse.success("Files retrieved successfully", files));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/shared")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<FileResponse>>> getSharedFiles(
            @RequestParam(required = false) String filename,
            @RequestParam(required = false) Long departmentCategoryId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long fileTypeId,
            @RequestParam(required = false) String contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            Long userId = securityUtil.getCurrentUserId()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            PageResponse<FileResponse> files = fileService.getSharedFiles(
                    userId, filename, departmentCategoryId, departmentId, projectId, fileTypeId, contentType, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(ApiResponse.success("Shared files retrieved successfully", files));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/shared/department/{departmentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<FileResponse>>> getSharedFilesByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(required = false) String filename,
            @RequestParam(required = false) Long departmentCategoryId,
            @RequestParam(required = false) Long fileTypeId,
            @RequestParam(required = false) String contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            Long userId = securityUtil.getCurrentUserId()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            PageResponse<FileResponse> files = fileService.getSharedFilesByDepartment(
                    userId, departmentId, filename, departmentCategoryId, fileTypeId, contentType, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(ApiResponse.success("Department shared files retrieved successfully", files));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/shared/project/{projectId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<FileResponse>>> getSharedFilesByProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) String filename,
            @RequestParam(required = false) Long fileTypeId,
            @RequestParam(required = false) String contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            Long userId = securityUtil.getCurrentUserId()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            PageResponse<FileResponse> files = fileService.getSharedFilesByProject(
                    userId, projectId, filename, fileTypeId, contentType, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(ApiResponse.success("Project shared files retrieved successfully", files));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FileResponse>> getFileById(@PathVariable Long id) {
        try {
            FileResponse file = fileService.getFileById(id)
                    .orElseThrow(() -> new RuntimeException("File not found"));
            return ResponseEntity.ok(ApiResponse.success("File retrieved successfully", file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable Long id) {
        try {
            boolean deleted = fileService.deleteFile(id);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("File not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            FileResponse file = fileService.getFileById(id)
                    .orElseThrow(() -> new RuntimeException("File not found"));
            
            byte[] fileContent = fileService.downloadFile(id);
            ByteArrayResource resource = new ByteArrayResource(fileContent);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + file.getOriginalFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}/preview")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Resource> previewFile(@PathVariable Long id) {
        try {
            FileResponse file = fileService.getFileById(id)
                    .orElseThrow(() -> new RuntimeException("File not found"));
            
            // Only allow preview for certain file types
            String contentType = file.getContentType();
            if (!isPreviewableType(contentType)) {
                return ResponseEntity.badRequest().build();
            }
            
            byte[] fileContent = fileService.downloadFile(id);
            ByteArrayResource resource = new ByteArrayResource(fileContent);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    private boolean isPreviewableType(String contentType) {
        return contentType != null && (
                contentType.startsWith("image/") ||
                contentType.startsWith("text/") ||
                contentType.equals("application/pdf") ||
                contentType.equals("application/json") ||
                contentType.equals("application/xml")
        );
    }
}
