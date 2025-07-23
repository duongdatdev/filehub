package com.duongdat.filehub.service.impl;

import com.duongdat.filehub.service.GoogleDriveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleDriveServiceImpl implements GoogleDriveService {
    
    @Value("${google.drive.enabled:false}")
    private boolean driveEnabled;
    
    @Value("${file.upload.directory:uploads}")
    private String localStorageDirectory;
    
    // Simulated Google Drive storage using local folder structure
    private String driveStorageDirectory;
    
    @PostConstruct
    public void initializeDriveService() {
        if (!driveEnabled) {
            log.warn("Google Drive simulation is disabled. Enable it by setting google.drive.enabled=true");
            return;
        }
        
        // Create a separate directory to simulate Google Drive storage
        driveStorageDirectory = localStorageDirectory + "/google-drive-simulation";
        try {
            Path drivePath = Paths.get(driveStorageDirectory);
            if (!Files.exists(drivePath)) {
                Files.createDirectories(drivePath);
            }
            log.info("Google Drive simulation initialized. Storage directory: {}", driveStorageDirectory);
        } catch (Exception e) {
            log.error("Failed to initialize Google Drive simulation: {}", e.getMessage(), e);
            driveEnabled = false;
        }
    }
    
    @Override
    public String uploadFile(MultipartFile file, String filename) throws Exception {
        if (!driveEnabled || driveStorageDirectory == null) {
            throw new RuntimeException("Google Drive simulation not available");
        }
        
        try {
            // Generate unique file ID to simulate Google Drive file ID
            String driveFileId = UUID.randomUUID().toString();
            
            // Create file path in drive simulation directory
            Path drivePath = Paths.get(driveStorageDirectory, driveFileId + "_" + filename);
            
            // Copy file to drive simulation directory
            Files.copy(file.getInputStream(), drivePath, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("File uploaded to Google Drive simulation: {} -> {}", filename, driveFileId);
            return driveFileId;
            
        } catch (Exception e) {
            log.error("Failed to upload file to Google Drive simulation: {}", e.getMessage(), e);
            throw new Exception("Google Drive upload failed: " + e.getMessage());
        }
    }
    
    @Override
    public byte[] downloadFile(String fileId) throws Exception {
        if (!driveEnabled || driveStorageDirectory == null) {
            throw new RuntimeException("Google Drive simulation not available");
        }
        
        try {
            // Find file by ID prefix in drive simulation directory
            Path driveDir = Paths.get(driveStorageDirectory);
            if (!Files.exists(driveDir)) {
                throw new Exception("Drive storage directory not found");
            }
            
            // Look for file starting with the fileId
            Path targetFile = Files.list(driveDir)
                    .filter(path -> path.getFileName().toString().startsWith(fileId + "_"))
                    .findFirst()
                    .orElseThrow(() -> new Exception("File not found in Google Drive simulation: " + fileId));
            
            byte[] fileContent = Files.readAllBytes(targetFile);
            log.info("File downloaded from Google Drive simulation: {}", fileId);
            return fileContent;
            
        } catch (Exception e) {
            log.error("Failed to download file from Google Drive simulation: {}", e.getMessage(), e);
            throw new Exception("Google Drive download failed: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteFile(String fileId) throws Exception {
        if (!driveEnabled || driveStorageDirectory == null) {
            throw new RuntimeException("Google Drive simulation not available");
        }
        
        try {
            // Find and delete file by ID prefix in drive simulation directory
            Path driveDir = Paths.get(driveStorageDirectory);
            if (!Files.exists(driveDir)) {
                return false;
            }
            
            // Look for file starting with the fileId and delete it
            boolean deleted = Files.list(driveDir)
                    .filter(path -> path.getFileName().toString().startsWith(fileId + "_"))
                    .findFirst()
                    .map(path -> {
                        try {
                            Files.delete(path);
                            log.info("File deleted from Google Drive simulation: {}", fileId);
                            return true;
                        } catch (Exception e) {
                            log.error("Failed to delete file: {}", e.getMessage());
                            return false;
                        }
                    })
                    .orElse(false);
            
            return deleted;
            
        } catch (Exception e) {
            log.error("Failed to delete file from Google Drive simulation: {}", e.getMessage(), e);
            throw new Exception("Google Drive delete failed: " + e.getMessage());
        }
    }
    
    @Override
    public String createFolder(String folderName, String parentFolderId) throws Exception {
        if (!driveEnabled || driveStorageDirectory == null) {
            throw new RuntimeException("Google Drive simulation not available");
        }
        
        try {
            // Generate unique folder ID
            String folderId = UUID.randomUUID().toString();
            
            // Create folder in drive simulation directory
            String folderPath = parentFolderId != null ? 
                    driveStorageDirectory + "/" + parentFolderId + "/" + folderName :
                    driveStorageDirectory + "/" + folderName;
            
            Path folder = Paths.get(folderPath);
            Files.createDirectories(folder);
            
            log.info("Folder created in Google Drive simulation: {} -> {}", folderName, folderId);
            return folderId;
            
        } catch (Exception e) {
            log.error("Failed to create folder in Google Drive simulation: {}", e.getMessage(), e);
            throw new Exception("Google Drive folder creation failed: " + e.getMessage());
        }
    }
}
