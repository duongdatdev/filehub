package com.duongdat.filehub.service.impl;

import com.duongdat.filehub.service.GoogleDriveService;
import com.duongdat.filehub.config.GoogleDriveProperties;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

@Service("googleDriveReal")
@Primary
@ConditionalOnProperty(name = "google.drive.use.real", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class GoogleDriveRealServiceImpl implements GoogleDriveService {
    
    private final GoogleDriveProperties driveProperties;
    
    private Drive driveService;
    private String rootFolderId;
    
    @PostConstruct
    public void initializeDriveService() {
        if (!driveProperties.isEnabled()) {
            log.warn("Google Drive is disabled. Enable it by setting google.drive.enabled=true");
            return;
        }
        
        try {
            log.info("Initializing real Google Drive service...");
            
            // Load credentials from service account key file
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new FileInputStream(driveProperties.getServiceAccountKeyPath()))
                    .createScoped(Collections.singletonList(DriveScopes.DRIVE));
            
            // Build Drive service
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            
            driveService = new Drive.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                    .setApplicationName(driveProperties.getApplicationName())
                    .build();
            
            // Create or find the root folder
            rootFolderId = createOrFindFolder(driveProperties.getFolderName(), null);
            
            log.info("Real Google Drive service initialized successfully. Root folder ID: {}", rootFolderId);
            
        } catch (Exception e) {
            log.error("Failed to initialize real Google Drive service: {}", e.getMessage(), e);
            throw new RuntimeException("Cannot initialize Google Drive service", e);
        }
    }
    
    @Override
    public String uploadFile(MultipartFile file, String filename) throws Exception {
        if (driveService == null) {
            throw new RuntimeException("Google Drive service not initialized");
        }
        
        try {
            // Create file metadata
            File fileMetadata = new File();
            fileMetadata.setName(filename);
            fileMetadata.setParents(Collections.singletonList(rootFolderId));
            
            // Create temporary file for upload
            Path tempFile = Files.createTempFile("upload-", filename);
            try {
                Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
                
                // Create media content
                FileContent mediaContent = new FileContent(file.getContentType(), tempFile.toFile());
                
                // Upload file
                File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                        .setFields("id,name,parents,size,createdTime")
                        .execute();
                
                log.info("File uploaded to real Google Drive: {} -> {}", filename, uploadedFile.getId());
                return uploadedFile.getId();
                
            } finally {
                // Clean up temporary file
                Files.deleteIfExists(tempFile);
            }
            
        } catch (Exception e) {
            log.error("Failed to upload file to real Google Drive: {}", e.getMessage(), e);
            throw new Exception("Real Google Drive upload failed: " + e.getMessage());
        }
    }
    
    @Override
    public byte[] downloadFile(String fileId) throws Exception {
        if (driveService == null) {
            throw new RuntimeException("Google Drive service not initialized");
        }
        
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
            
            byte[] fileContent = outputStream.toByteArray();
            log.info("File downloaded from real Google Drive: {}", fileId);
            return fileContent;
            
        } catch (Exception e) {
            log.error("Failed to download file from real Google Drive: {}", e.getMessage(), e);
            throw new Exception("Real Google Drive download failed: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteFile(String fileId) throws Exception {
        if (driveService == null) {
            throw new RuntimeException("Google Drive service not initialized");
        }
        
        try {
            driveService.files().delete(fileId).execute();
            log.info("File deleted from real Google Drive: {}", fileId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to delete file from real Google Drive: {}", e.getMessage(), e);
            throw new Exception("Real Google Drive delete failed: " + e.getMessage());
        }
    }
    
    @Override
    public String createFolder(String folderName, String parentFolderId) throws Exception {
        if (driveService == null) {
            throw new RuntimeException("Google Drive service not initialized");
        }
        
        try {
            File fileMetadata = new File();
            fileMetadata.setName(folderName);
            fileMetadata.setMimeType("application/vnd.google-apps.folder");
            
            if (parentFolderId != null) {
                fileMetadata.setParents(Collections.singletonList(parentFolderId));
            }
            
            File folder = driveService.files().create(fileMetadata)
                    .setFields("id,name,parents")
                    .execute();
            
            log.info("Folder created in real Google Drive: {} -> {}", folderName, folder.getId());
            return folder.getId();
            
        } catch (Exception e) {
            log.error("Failed to create folder in real Google Drive: {}", e.getMessage(), e);
            throw new Exception("Real Google Drive folder creation failed: " + e.getMessage());
        }
    }
    
    private String createOrFindFolder(String folderName, String parentFolderId) throws IOException {
        // Search for existing folder
        String query = "name='" + folderName + "' and mimeType='application/vnd.google-apps.folder' and trashed=false";
        if (parentFolderId != null) {
            query += " and '" + parentFolderId + "' in parents";
        }
        
        FileList result = driveService.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute();
        
        List<File> folders = result.getFiles();
        if (!folders.isEmpty()) {
            String folderId = folders.get(0).getId();
            log.info("Found existing folder: {} -> {}", folderName, folderId);
            return folderId;
        }
        
        // Create new folder if not found
        try {
            return createFolder(folderName, parentFolderId);
        } catch (Exception e) {
            throw new IOException("Failed to create folder: " + e.getMessage(), e);
        }
    }
}
