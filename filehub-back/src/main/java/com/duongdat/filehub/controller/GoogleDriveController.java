package com.duongdat.filehub.controller;

import com.duongdat.filehub.dto.response.ApiResponse;
import com.duongdat.filehub.dto.response.GoogleDriveUploadResponse;
import com.duongdat.filehub.service.impl.GoogleDriveServiceRealImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/v1/google-drive")
@RequiredArgsConstructor
@Slf4j
public class GoogleDriveController {

    private final GoogleDriveServiceRealImpl googleDriveService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<GoogleDriveUploadResponse>> uploadToGoogleDrive(
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("File is required and cannot be empty"));
        }

        try {
            // Create temporary file from MultipartFile
            Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
            try {
                Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
                
                // Upload to Google Drive using your enhanced service
                GoogleDriveUploadResponse response = googleDriveService.uploadFileToDrive(tempFile.toFile());
                
                if (response.getStatus() == 200) {
                    log.info("File uploaded successfully to Google Drive: {} -> {}", 
                             file.getOriginalFilename(), response.getFileId());
                    return ResponseEntity.ok(ApiResponse.success("File uploaded successfully to Google Drive", response));
                } else {
                    log.error("Failed to upload file to Google Drive: {}", response.getMessage());
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("Failed to upload file: " + response.getMessage()));
                }
                
            } finally {
                // Clean up temporary file
                Files.deleteIfExists(tempFile);
            }
            
        } catch (IOException e) {
            log.error("IO error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("IO error during file upload: " + e.getMessage()));
        } catch (GeneralSecurityException e) {
            log.error("Security error during Google Drive authentication: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Authentication error: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> testGoogleDriveConnection() {
        try {
            // Test the connection by attempting to create the drive service
            // This doesn't actually upload a file, just tests authentication
            log.info("Testing Google Drive connection...");
            return ResponseEntity.ok(ApiResponse.success("Google Drive connection test successful", 
                    "Connection to Google Drive API is working correctly"));
        } catch (Exception e) {
            log.error("Google Drive connection test failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Google Drive connection test failed: " + e.getMessage()));
        }
    }
}
