package com.duongdat.filehub.service.impl;

import com.duongdat.filehub.service.GoogleDriveService;
import com.duongdat.filehub.dto.response.GoogleDriveUploadResponse;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

@Service("googleDriveReal")
@Primary
@ConditionalOnProperty(name = "google.drive.use.real", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class GoogleDriveServiceRealImpl implements GoogleDriveService {
    
    @Value("${google.drive.enabled:false}")
    private boolean driveEnabled;
    
    @Value("${google.drive.oauth2.client.secrets.path:src/main/resources/credentials.json}")
    private String clientSecretsPath;
    
    @Value("${google.drive.oauth2.tokens.directory:tokens}")
    private String tokensDirectoryPath;
    
    @Value("${google.drive.application.name:FileHub}")
    private String applicationName;
    
    @Value("${google.drive.folder.name:FileHub-Storage}")
    private String folderName;
    
    @Value("${google.drive.use.real:false}")
    private boolean useRealGoogleDrive;
    
    // Fallback to simulation
    @Value("${file.upload.directory:uploads}")
    private String localStorageDirectory;
    
    private Drive driveService;
    private String rootFolderId;
    private String driveStorageDirectory; // For simulation fallback if real Drive is not available
    private Credential credential; // Store OAuth2 credential for reuse
    
    @Autowired(required = false)
    private Drive configuredDriveService; // Injected from configuration if available
    
    @Autowired
    private GoogleDriveTokenRefreshService tokenRefreshService;
    
    @PostConstruct
    public void initializeDriveService() {
        if (!driveEnabled) {
            log.warn("Google Drive is disabled. Enable it by setting google.drive.enabled=true");
            return;
        }
        
        if (useRealGoogleDrive) {
            initializeRealGoogleDrive();
        } else {
            initializeSimulation();
        }
    }
    
    private void initializeRealGoogleDrive() {
        try {
            log.info("Initializing real Google Drive service with OAuth2...");
            
            // Use configured Drive service if available, otherwise create our own
            if (configuredDriveService != null) {
                log.info("Using pre-configured Drive service from OAuth2 configuration");
                driveService = configuredDriveService;
            } else {
                log.info("Creating Drive service with inline OAuth2 flow");
                // Get OAuth2 credential
                credential = getOAuth2Credential();
                
                // Build Drive service
                HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
                
                driveService = new Drive.Builder(httpTransport, jsonFactory, credential)
                        .setApplicationName(applicationName)
                        .build();
            }
            
            // Create or find the root folder
            rootFolderId = "1nnUw8er5hTOJRdc3WIsGPT9QXJlk0d-L";
            
            log.info("Real Google Drive service initialized successfully with OAuth2. Root folder ID: {}", rootFolderId);
            
        } catch (Exception e) {
            log.error("Failed to initialize real Google Drive service with OAuth2: {}", e.getMessage(), e);
            log.warn("Falling back to Google Drive simulation...");
            useRealGoogleDrive = false;
            initializeSimulation();
        }
    }
    
    /**
     * Get OAuth2 credential for Google Drive API access with automatic refresh
     */
    private Credential getOAuth2Credential() throws Exception {
        try {
            return tokenRefreshService.getCredentialWithRefresh();
        } catch (Exception e) {
            log.error("Failed to get OAuth2 credential with refresh service: {}", e.getMessage(), e);
            throw new Exception("OAuth2 authentication failed: " + e.getMessage());
        }
    }
    
    private void initializeSimulation() {
        log.info("Initializing Google Drive simulation...");
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
        if (!driveEnabled) {
            throw new RuntimeException("Google Drive service not available");
        }
        
        if (useRealGoogleDrive && driveService != null) {
            return uploadToRealGoogleDrive(file, filename);
        } else {
            return uploadToSimulation(file, filename);
        }
    }
    
    private String uploadToRealGoogleDrive(MultipartFile file, String filename) throws Exception {
        try {
            // Validate and refresh credential if needed
            if (credential != null && !tokenRefreshService.isCredentialValid(credential)) {
                log.warn("Current credential is invalid, attempting to get new credential...");
                credential = getOAuth2Credential();
                // Recreate drive service with new credential
                HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
                driveService = new Drive.Builder(httpTransport, jsonFactory, credential)
                        .setApplicationName(applicationName)
                        .build();
            }
            
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
            
            // If it's a token-related error, try to clear tokens and suggest re-authorization
            if (e.getMessage().contains("invalid_grant") || e.getMessage().contains("Token has been expired")) {
                log.warn("OAuth2 token has expired or been revoked. Clearing stored tokens...");
                tokenRefreshService.clearStoredTokens();
                throw new Exception("OAuth2 token expired. Please restart the application to re-authorize Google Drive access.");
            }
            
            throw new Exception("Real Google Drive upload failed: " + e.getMessage());
        }
    }
    
    private String uploadToSimulation(MultipartFile file, String filename) throws Exception {
        try {
            // Generate unique file ID to simulate Google Drive file ID
            String driveFileId = java.util.UUID.randomUUID().toString();
            
            // Create file path in drive simulation directory
            Path drivePath = Paths.get(driveStorageDirectory, driveFileId + "_" + filename);
            
            // Copy file to drive simulation directory
            Files.copy(file.getInputStream(), drivePath, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("File uploaded to Google Drive simulation: {} -> {}", filename, driveFileId);
            return driveFileId;
            
        } catch (Exception e) {
            log.error("Failed to upload file to Google Drive simulation: {}", e.getMessage(), e);
            throw new Exception("Google Drive simulation upload failed: " + e.getMessage());
        }
    }
    
    @Override
    public byte[] downloadFile(String fileId) throws Exception {
        if (!driveEnabled) {
            throw new RuntimeException("Google Drive service not available");
        }
        
        if (useRealGoogleDrive && driveService != null) {
            return downloadFromRealGoogleDrive(fileId);
        } else {
            return downloadFromSimulation(fileId);
        }
    }
    
    private byte[] downloadFromRealGoogleDrive(String fileId) throws Exception {
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
    
    private byte[] downloadFromSimulation(String fileId) throws Exception {
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
            throw new Exception("Google Drive simulation download failed: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteFile(String fileId) throws Exception {
        if (!driveEnabled) {
            throw new RuntimeException("Google Drive service not available");
        }
        
        if (useRealGoogleDrive && driveService != null) {
            return deleteFromRealGoogleDrive(fileId);
        } else {
            return deleteFromSimulation(fileId);
        }
    }
    
    private boolean deleteFromRealGoogleDrive(String fileId) throws Exception {
        try {
            driveService.files().delete(fileId).execute();
            log.info("File deleted from real Google Drive: {}", fileId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to delete file from real Google Drive: {}", e.getMessage(), e);
            throw new Exception("Real Google Drive delete failed: " + e.getMessage());
        }
    }
    
    private boolean deleteFromSimulation(String fileId) throws Exception {
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
            throw new Exception("Google Drive simulation delete failed: " + e.getMessage());
        }
    }
    
    @Override
    public String createFolder(String folderName, String parentFolderId) throws Exception {
        if (!driveEnabled) {
            throw new RuntimeException("Google Drive service not available");
        }
        
        if (useRealGoogleDrive && driveService != null) {
            return createFolderInRealGoogleDrive(folderName, parentFolderId);
        } else {
            return createFolderInSimulation(folderName, parentFolderId);
        }
    }
    
    private String createFolderInRealGoogleDrive(String folderName, String parentFolderId) throws Exception {
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
    
    private String createFolderInSimulation(String folderName, String parentFolderId) throws Exception {
        try {
            // Generate unique folder ID
            String folderId = java.util.UUID.randomUUID().toString();
            
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
            throw new Exception("Google Drive simulation folder creation failed: " + e.getMessage());
        }
    }

    /**
     * Upload file to Google Drive and return response with URL
     * This method is used by the GoogleDriveController
     */
    public GoogleDriveUploadResponse uploadFileToDrive(java.io.File file) throws Exception {
        GoogleDriveUploadResponse res = new GoogleDriveUploadResponse();

        try {
            // Use the configured folder ID or rootFolderId
            Drive drive = createRealDriveService();
            
            com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
            fileMetaData.setName(file.getName());
            fileMetaData.setParents(Collections.singletonList(rootFolderId != null ? rootFolderId : "1nnUw8er5hTOJRdc3WIsGPT9QXJlk0d-L"));
            
            // Determine content type based on file extension
            String contentType = determineContentType(file.getName());
            FileContent mediaContent = new FileContent(contentType, file);
            
            com.google.api.services.drive.model.File uploadedFile = drive.files().create(fileMetaData, mediaContent)
                    .setFields("id").execute();
                    
            String fileUrl = "https://drive.google.com/uc?export=view&id=" + uploadedFile.getId();
            log.info("FILE URL: {}", fileUrl);
            
            // Clean up the temporary file
            file.delete();
            
            res.setStatus(200);
            res.setMessage("File Successfully Uploaded To Drive");
            res.setUrl(fileUrl);
            res.setFileId(uploadedFile.getId());
            
        } catch (Exception e) {
            log.error("Error uploading file to Drive: {}", e.getMessage(), e);
            res.setStatus(500);
            res.setMessage(e.getMessage());
        }
        return res;
    }

    private Drive createRealDriveService() throws Exception {
        // Use configured Drive service if available
        if (configuredDriveService != null) {
            return configuredDriveService;
        }
        
        // Use existing credential or get a new one
        if (credential == null) {
            credential = getOAuth2Credential();
        }
        
        // Build Drive service
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        
        return new Drive.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(applicationName)
                .build();
    }
    
    private String determineContentType(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        
        if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerFileName.endsWith(".png")) {
            return "image/png";
        } else if (lowerFileName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerFileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerFileName.endsWith(".doc")) {
            return "application/msword";
        } else if (lowerFileName.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (lowerFileName.endsWith(".txt")) {
            return "text/plain";
        } else if (lowerFileName.endsWith(".csv")) {
            return "text/csv";
        } else if (lowerFileName.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (lowerFileName.endsWith(".xls")) {
            return "application/vnd.ms-excel";
        } else if (lowerFileName.endsWith(".ppt")) {
            return "application/vnd.ms-powerpoint";
        } else if (lowerFileName.endsWith(".pptx")) {
            return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        } else {
            return "application/octet-stream";
        }
    }
}
