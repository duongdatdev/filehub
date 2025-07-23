package com.duongdat.filehub.controller;

import com.duongdat.filehub.dto.request.FileUploadRequest;
import com.duongdat.filehub.dto.response.ApiResponse;
import com.duongdat.filehub.dto.response.FileResponse;
import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.service.FileService;
import com.duongdat.filehub.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class FileController {
    
    private final FileService fileService;
    private final SecurityUtil securityUtil;
    
    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FileResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute FileUploadRequest request) {
        try {
            FileResponse fileResponse = fileService.uploadFile(file, request);
            return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", fileResponse));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to upload file: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<FileResponse>>> getUserFiles(
            @RequestParam(required = false) String filename,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            Long userId = securityUtil.getCurrentUserId()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            PageResponse<FileResponse> files = fileService.getUserFiles(
                    userId, filename, categoryId, contentType, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(ApiResponse.success("Files retrieved successfully", files));
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
