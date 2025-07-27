package com.duongdat.filehub.controller;

import com.duongdat.filehub.dto.request.FileUploadRequest;
import com.duongdat.filehub.dto.response.ApiResponse;
import com.duongdat.filehub.dto.response.FileResponse;
import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.service.FileService;
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
    
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FileResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "departmentCategoryId", required = false) Long departmentCategoryId,
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "projectId", required = false) Long projectId,
            @RequestParam(value = "fileTypeId", required = false) Long fileTypeId,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "visibility", defaultValue = "PRIVATE") String visibility) {
        try {
            log.debug("File upload request received - File: {}, Title: {}, Description: {}, DepartmentCategoryId: {}, DepartmentId: {}, ProjectId: {}, FileTypeId: {}, Tags: {}, Visibility: {}", 
                      file != null ? file.getOriginalFilename() : "null", title, description, departmentCategoryId, departmentId, projectId, fileTypeId, tags, visibility);
            
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
            return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", fileResponse));
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
