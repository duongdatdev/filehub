package com.duongdat.filehub.service;

import com.duongdat.filehub.dto.request.FileUploadRequest;
import com.duongdat.filehub.dto.response.FileResponse;
import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.entity.File;
import com.duongdat.filehub.repository.FileRepository;
import com.duongdat.filehub.repository.FileCategoryRepository;
import com.duongdat.filehub.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    
    private final FileRepository fileRepository;
    private final FileCategoryRepository fileCategoryRepository;
    private final SecurityUtil securityUtil;
    private final GoogleDriveService googleDriveService;
    
    @Value("${file.upload.directory:uploads}")
    private String uploadDirectory;
    
    @Value("${file.storage.primary:google-drive}")
    private String primaryStorage;
    
    @Value("${file.storage.fallback:local}")
    private String fallbackStorage;
    
    @Value("${file.max.size:104857600}") // 100MB default
    private long maxFileSize;
    
    public FileResponse uploadFile(MultipartFile multipartFile, FileUploadRequest request) throws IOException {
        // Validate file
        validateFile(multipartFile);
        
        // Get current user
        Long userId = securityUtil.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));
        
        // Check for duplicate file by hash
        String fileHash = calculateFileHash(multipartFile.getBytes());
        Optional<File> existingFile = fileRepository.findByFileHash(fileHash);
        if (existingFile.isPresent()) {
            throw new RuntimeException("File already exists");
        }
        
        // Generate unique filename
        String originalFilename = multipartFile.getOriginalFilename();
        String storedFilename = generateUniqueFilename(originalFilename);
        
        // Create file entity
        File file = new File();
        file.setUserId(userId);
        file.setOriginalFilename(originalFilename);
        file.setStoredFilename(storedFilename);
        file.setFileSize(multipartFile.getSize());
        file.setContentType(multipartFile.getContentType());
        file.setFileHash(fileHash);
        file.setTitle(request.getTitle() != null ? request.getTitle() : originalFilename);
        file.setDescription(request.getDescription());
        file.setCategoryId(request.getCategoryId());
        // Handle tags properly for JSON column - convert empty/null to null or valid JSON
        String tags = request.getTags();
        if (tags == null || tags.trim().isEmpty()) {
            file.setTags(null); // NULL is valid for JSON column
        } else {
            file.setTags(tags);
        }
        file.setVisibility(request.getVisibility());
        
        // Primary storage: Upload to Google Drive first
        String driveFileId = null;
        boolean driveUploadSuccess = false;
        
        if ("google-drive".equals(primaryStorage)) {
            try {
                driveFileId = googleDriveService.uploadFile(multipartFile, storedFilename);
                if (driveFileId != null) {
                    file.setDriveFileId(driveFileId);
                    driveUploadSuccess = true;
                    log.info("File uploaded to Google Drive (primary): {}", driveFileId);
                }
            } catch (Exception e) {
                log.warn("Failed to upload to Google Drive (primary), falling back to local storage: {}", e.getMessage());
            }
        }
        
        // Fallback storage: Store locally if Google Drive fails or as backup
        if (!driveUploadSuccess || "local".equals(fallbackStorage)) {
            try {
                Path uploadPath = Paths.get(uploadDirectory);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                Path filePath = uploadPath.resolve(storedFilename);
                Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                file.setFilePath(filePath.toString());
                log.info("File stored locally: {}", filePath);
                
                // If primary storage failed, mark as local-only
                if (!driveUploadSuccess) {
                    log.info("File stored in fallback storage (local): {}", filePath);
                }
            } catch (Exception e) {
                if (!driveUploadSuccess) {
                    // Both storages failed
                    throw new IOException("Failed to store file in both primary and fallback storage: " + e.getMessage());
                }
                log.warn("Failed to store in fallback storage, but primary storage succeeded: {}", e.getMessage());
            }
        }
        
        // Save to database
        file = fileRepository.save(file);
        
        return convertToFileResponse(file);
    }
    
    public PageResponse<FileResponse> getUserFiles(Long userId, String filename, Long categoryId, 
                                                 String contentType, int page, int size, 
                                                 String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<File> filesPage = fileRepository.findFilesWithFilters(userId, filename, categoryId, contentType, pageable);
        
        return new PageResponse<FileResponse>(
                filesPage.getContent().stream().map(this::convertToFileResponse).toList(),
                filesPage.getNumber(),
                filesPage.getSize(),
                filesPage.getTotalElements(),
                filesPage.getTotalPages(),
                filesPage.isFirst(),
                filesPage.isLast(),
                filesPage.hasNext(),
                filesPage.hasPrevious()
        );
    }
    
    public Optional<FileResponse> getFileById(Long fileId) {
        Long userId = securityUtil.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));
        
        return fileRepository.findByIdAndUserIdAndIsDeletedFalse(fileId, userId)
                .map(this::convertToFileResponse);
    }
    
    public boolean deleteFile(Long fileId) {
        Long userId = securityUtil.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));
        
        Optional<File> fileOpt = fileRepository.findByIdAndUserIdAndIsDeletedFalse(fileId, userId);
        if (fileOpt.isPresent()) {
            File file = fileOpt.get();
            
            // Mark as deleted in database
            file.setIsDeleted(true);
            file.setDeletedAt(LocalDateTime.now());
            fileRepository.save(file);
            
            // Delete from primary storage (Google Drive)
            if ("google-drive".equals(primaryStorage) && file.getDriveFileId() != null) {
                try {
                    boolean driveDeleted = googleDriveService.deleteFile(file.getDriveFileId());
                    if (driveDeleted) {
                        log.info("File deleted from Google Drive (primary): {}", file.getDriveFileId());
                    }
                } catch (Exception e) {
                    log.warn("Failed to delete from Google Drive (primary): {}", e.getMessage());
                }
            }
            
            // Delete from fallback storage (local) if exists
            if (file.getFilePath() != null) {
                try {
                    Path filePath = Paths.get(file.getFilePath());
                    if (Files.exists(filePath)) {
                        Files.delete(filePath);
                        log.info("File deleted from local storage (fallback): {}", filePath);
                    }
                } catch (Exception e) {
                    log.warn("Failed to delete from local storage: {}", e.getMessage());
                }
            }
            
            return true;
        }
        return false;
    }
    
    public byte[] downloadFile(Long fileId) throws IOException {
        Long userId = securityUtil.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));
        
        File file = fileRepository.findByIdAndUserIdAndIsDeletedFalse(fileId, userId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        
        // Primary storage: Try to download from Google Drive first
        if ("google-drive".equals(primaryStorage) && file.getDriveFileId() != null) {
            try {
                byte[] driveContent = googleDriveService.downloadFile(file.getDriveFileId());
                if (driveContent != null) {
                    // Increment download count
                    file.setDownloadCount(file.getDownloadCount() + 1);
                    fileRepository.save(file);
                    log.info("File downloaded from Google Drive (primary): {}", file.getDriveFileId());
                    return driveContent;
                }
            } catch (Exception e) {
                log.warn("Failed to download from Google Drive (primary), trying fallback storage: {}", e.getMessage());
            }
        }
        
        // Fallback storage: Try local file
        if (file.getFilePath() != null) {
            try {
                Path filePath = Paths.get(file.getFilePath());
                if (Files.exists(filePath)) {
                    // Increment download count
                    file.setDownloadCount(file.getDownloadCount() + 1);
                    fileRepository.save(file);
                    log.info("File downloaded from local storage (fallback): {}", filePath);
                    return Files.readAllBytes(filePath);
                }
            } catch (Exception e) {
                log.error("Failed to download from local storage: {}", e.getMessage());
            }
        }
        
        throw new RuntimeException("File not found in any storage location");
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("File size exceeds maximum allowed size");
        }
        
        // Add more validation as needed (file type, etc.)
    }
    
    private String calculateFileHash(byte[] fileContent) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(fileContent);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating file hash", e);
        }
    }
    
    private String generateUniqueFilename(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
    
    private FileResponse convertToFileResponse(File file) {
        FileResponse response = new FileResponse();
        response.setId(file.getId());
        response.setOriginalFilename(file.getOriginalFilename());
        response.setTitle(file.getTitle());
        response.setDescription(file.getDescription());
        response.setFileSize(file.getFileSize());
        response.setContentType(file.getContentType());
        response.setTags(file.getTags());
        response.setVisibility(file.getVisibility());
        response.setDownloadCount(file.getDownloadCount());
        response.setVersion(file.getVersion());
        response.setUploadedAt(file.getUploadedAt());
        response.setUpdatedAt(file.getUpdatedAt());
        response.setCategoryId(file.getCategoryId());
        response.setDriveFileId(file.getDriveFileId());
        response.setDriveFolderId(file.getDriveFolderId());
        
        // Set category name if category exists
        if (file.getCategoryId() != null) {
            fileCategoryRepository.findById(file.getCategoryId())
                    .ifPresent(category -> response.setCategoryName(category.getName()));
        }
        
        // Generate download URL
        response.setDownloadUrl("/api/files/" + file.getId() + "/download");
        response.setPreviewUrl("/api/files/" + file.getId() + "/preview");
        
        return response;
    }
}
