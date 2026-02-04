package com.duongdat.filehub.service;

import com.duongdat.filehub.dto.request.FileUploadRequest;
import com.duongdat.filehub.dto.response.FileResponse;
import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.entity.File;
import com.duongdat.filehub.repository.FileRepository;
import com.duongdat.filehub.repository.UserRepository;
import com.duongdat.filehub.repository.DepartmentCategoryRepository;
import com.duongdat.filehub.repository.FileTypeRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final DepartmentCategoryRepository departmentCategoryRepository;
    private final FileTypeRepository fileTypeRepository;
    private final SecurityUtil securityUtil;
    private final GoogleDriveService googleDriveService;
    private final UserAuthorizationService userAuthorizationService;
    
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
        
        // Validate required fields before processing
        validateUploadRequest(request);
        
        // Validate upload permissions - NEW AUTHORIZATION LOGIC
        userAuthorizationService.validateFileUploadPermissions(request.getDepartmentId(), request.getProjectId());
        
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
        file.setUploaderId(userId);
        file.setOriginalFilename(originalFilename);
        file.setStoredFilename(storedFilename);
        file.setFileSize(multipartFile.getSize());
        file.setContentType(multipartFile.getContentType());
        file.setFileHash(fileHash);
        file.setTitle(request.getTitle() != null ? request.getTitle() : originalFilename);
        file.setDescription(request.getDescription());
        file.setDepartmentCategoryId(request.getDepartmentCategoryId());
        file.setDepartmentId(request.getDepartmentId());
        file.setProjectId(request.getProjectId());
        file.setFileTypeId(request.getFileTypeId());
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
    
    public PageResponse<FileResponse> getUserFiles(Long userId, String filename, Long departmentCategoryId, 
                                                 Long departmentId, Long projectId, Long fileTypeId, String contentType, 
                                                 int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Apply authorization filters
        Page<File> filesPage;
        if (userAuthorizationService.isUserAdmin(userId)) {
            // Admin can see all files
            filesPage = fileRepository.findFilesWithFilters(userId, filename, departmentCategoryId, departmentId, projectId, fileTypeId, contentType, pageable);
        } else {
            // Regular users can only see files from their departments/projects
            List<Long> accessibleDepartmentIds = userAuthorizationService.getAccessibleDepartmentIds();
            List<Long> accessibleProjectIds = userAuthorizationService.getAccessibleProjectIds();
            
            // If user has no accessible departments/projects, they can only see public files
            if (accessibleDepartmentIds.isEmpty()) {
                accessibleDepartmentIds = List.of(-1L); // Use -1 as placeholder for no access
            }
            if (accessibleProjectIds.isEmpty()) {
                accessibleProjectIds = List.of(-1L);
            }
            
            // Filter by accessible departments and projects
            filesPage = fileRepository.findFilesWithAuthorizationFilters(
                userId, filename, departmentCategoryId, departmentId, projectId, fileTypeId, contentType,
                accessibleDepartmentIds, accessibleProjectIds, pageable);
        }
        
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
    
    // Admin method to get all files with filters
    public PageResponse<FileResponse> getAllFilesWithFilters(String filename, Long departmentCategoryId, 
                                                           Long departmentId, Long projectId, Long uploaderId, Long fileTypeId,
                                                           String contentType, int page, int size, 
                                                           String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<File> filesPage = fileRepository.findAllFilesWithFilters(filename, departmentCategoryId, departmentId, projectId, uploaderId, fileTypeId, contentType, pageable);
        
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
    
    public List<FileResponse> getFilesByDepartment(Long departmentId) {
        // Check if current user can view files in this department
        if (!userAuthorizationService.canViewDepartmentFiles(departmentId)) {
            throw new RuntimeException("You don't have permission to view files in this department");
        }
        
        // Only get department files that don't belong to projects
        List<File> files = fileRepository.findByDepartmentIdAndProjectIdIsNullAndIsDeletedFalse(departmentId);
        return files.stream().map(this::convertToFileResponse).toList();
    }
    
    public List<FileResponse> getFilesByProject(Long projectId) {
        // Check if current user can view files in this project
        if (!userAuthorizationService.canViewProjectFiles(projectId)) {
            throw new RuntimeException("You don't have permission to view files in this project");
        }
        
        List<File> files = fileRepository.findByProjectIdAndIsDeletedFalse(projectId);
        return files.stream().map(this::convertToFileResponse).toList();
    }

    public PageResponse<FileResponse> getSharedFiles(Long userId, String filename, Long departmentCategoryId, 
                                                   Long departmentId, Long projectId, Long fileTypeId, String contentType, 
                                                   int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // If user is admin, they can see all files
        Page<File> filesPage;
        if (userAuthorizationService.isUserAdmin(userId)) {
            // Admin can see all files
            filesPage = fileRepository.findAllFilesWithFilters(filename, departmentCategoryId, departmentId, projectId, null, fileTypeId, contentType, pageable);
        } else {
            // Get accessible departments and projects for the user
            List<Long> accessibleDepartmentIds = userAuthorizationService.getAccessibleDepartmentIds();
            List<Long> accessibleProjectIds = userAuthorizationService.getAccessibleProjectIds();
            
            // If user has no accessible departments/projects, they can only see public files
            if (accessibleDepartmentIds.isEmpty()) {
                accessibleDepartmentIds = List.of(-1L); // Use -1 as placeholder for no access
            }
            if (accessibleProjectIds.isEmpty()) {
                accessibleProjectIds = List.of(-1L);
            }
            
            // Get all files (including from other users) in accessible departments and projects
            filesPage = fileRepository.findSharedFilesWithAuthorizationFilters(
                filename, departmentCategoryId, departmentId, projectId, fileTypeId, contentType,
                accessibleDepartmentIds, accessibleProjectIds, pageable);
        }
        
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

    public PageResponse<FileResponse> getSharedFilesByDepartment(Long userId, Long departmentId, String filename, 
                                                               Long departmentCategoryId, Long fileTypeId, String contentType, 
                                                               int page, int size, String sortBy, String sortDirection) {
        // Check if current user can view files in this department
        if (!userAuthorizationService.canViewDepartmentFiles(departmentId)) {
            throw new RuntimeException("You don't have permission to view files in this department");
        }
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<File> filesPage = fileRepository.findSharedFilesByDepartment(
            departmentId, filename, departmentCategoryId, fileTypeId, contentType, pageable);
        
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

    public PageResponse<FileResponse> getSharedFilesByProject(Long userId, Long projectId, String filename, 
                                                            Long fileTypeId, String contentType, 
                                                            int page, int size, String sortBy, String sortDirection) {
        // Check if current user can view files in this project
        if (!userAuthorizationService.canViewProjectFiles(projectId)) {
            throw new RuntimeException("You don't have permission to view files in this project");
        }
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<File> filesPage = fileRepository.findSharedFilesByProject(
            projectId, filename, fileTypeId, contentType, pageable);
        
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
        
        // First try to find the file
        Optional<File> fileOpt = fileRepository.findById(fileId);
        if (fileOpt.isEmpty() || fileOpt.get().getIsDeleted()) {
            return Optional.empty();
        }
        
        File file = fileOpt.get();
        
        // Check if user can access this file
        boolean canAccess = false;
        
        // Admin can access all files
        if (userAuthorizationService.isUserAdmin(userId)) {
            canAccess = true;
        }
        // User uploaded this file
        else if (file.getUploaderId().equals(userId)) {
            canAccess = true;
        }
        // File is public
        else if ("PUBLIC".equals(file.getVisibility())) {
            canAccess = true;
        }
        // Project files: only accessible to project members
        else if (file.getProjectId() != null && userAuthorizationService.canViewProjectFiles(file.getProjectId())) {
            canAccess = true;
        }
        // Department files (without project): accessible to department members
        else if (file.getProjectId() == null && file.getDepartmentId() != null && userAuthorizationService.canViewDepartmentFiles(file.getDepartmentId())) {
            canAccess = true;
        }
        
        if (!canAccess) {
            throw new RuntimeException("You don't have permission to access this file");
        }
        
        return Optional.of(convertToFileResponse(file));
    }
    
    public boolean deleteFile(Long fileId) {
        Long userId = securityUtil.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));
        
        // Find the file first
        Optional<File> fileOpt = fileRepository.findById(fileId);
        if (fileOpt.isEmpty() || fileOpt.get().getIsDeleted()) {
            return false;
        }
        
        File file = fileOpt.get();
        
        // Check if user can delete this file (only owner or admin)
        boolean canDelete = false;
        if (userAuthorizationService.isUserAdmin(userId)) {
            canDelete = true;
        } else if (file.getUploaderId().equals(userId)) {
            canDelete = true;
        }
        
        if (!canDelete) {
            throw new RuntimeException("You don't have permission to delete this file");
        }
        
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
    
    public byte[] downloadFile(Long fileId) throws IOException {
        Long userId = securityUtil.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));
        
        // First try to find the file
        Optional<File> fileOpt = fileRepository.findById(fileId);
        if (fileOpt.isEmpty() || fileOpt.get().getIsDeleted()) {
            throw new RuntimeException("File not found");
        }
        
        File file = fileOpt.get();
        
        // Check if user can access this file
        boolean canAccess = false;
        
        // Admin can access all files
        if (userAuthorizationService.isUserAdmin(userId)) {
            canAccess = true;
        }
        // User uploaded this file
        else if (file.getUploaderId().equals(userId)) {
            canAccess = true;
        }
        // File is public
        else if ("PUBLIC".equals(file.getVisibility())) {
            canAccess = true;
        }
        // Project files: only accessible to project members
        else if (file.getProjectId() != null && userAuthorizationService.canViewProjectFiles(file.getProjectId())) {
            canAccess = true;
        }
        // Department files (without project): accessible to department members
        else if (file.getProjectId() == null && file.getDepartmentId() != null && userAuthorizationService.canViewDepartmentFiles(file.getDepartmentId())) {
            canAccess = true;
        }
        
        if (!canAccess) {
            throw new RuntimeException("You don't have permission to access this file");
        }
        
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
        response.setFileHash(file.getFileHash());
        response.setUploaderId(file.getUploaderId());
        response.setDepartmentId(file.getDepartmentId());
        response.setDepartmentCategoryId(file.getDepartmentCategoryId());
        response.setFileTypeId(file.getFileTypeId());
        response.setProjectId(file.getProjectId());
        response.setTags(file.getTags());
        response.setVisibility(file.getVisibility());
        response.setDownloadCount(file.getDownloadCount());
        response.setIsDeleted(file.getIsDeleted());
        response.setUploadedAt(file.getUploadedAt());
        response.setUpdatedAt(file.getUpdatedAt());
        response.setDeletedAt(file.getDeletedAt());
        response.setDriveFileId(file.getDriveFileId());
        response.setDriveFolderId(file.getDriveFolderId());
        
        // Set uploader name if uploader exists
        if (file.getUploaderId() != null) {
            userRepository.findById(file.getUploaderId())
                    .ifPresent(user -> response.setUploaderName(user.getFullName()));
        }
        
        // Set department category name if category exists
        if (file.getDepartmentCategoryId() != null) {
            departmentCategoryRepository.findById(file.getDepartmentCategoryId())
                    .ifPresent(category -> response.setDepartmentCategoryName(category.getName()));
        }
        
        // Set file type name if file type exists
        if (file.getFileTypeId() != null) {
            fileTypeRepository.findById(file.getFileTypeId())
                    .ifPresent(fileType -> response.setFileTypeName(fileType.getName()));
        }
        
        // Set department name if department exists  
        if (file.getDepartmentId() != null && file.getDepartment() != null) {
            response.setDepartmentName(file.getDepartment().getName());
        }
        
        // Set project name if project exists
        if (file.getProjectId() != null && file.getProject() != null) {
            response.setProjectName(file.getProject().getName());
        }
        
        // Generate download URL
        response.setDownloadUrl("/api/files/" + file.getId() + "/download");
        response.setPreviewUrl("/api/files/" + file.getId() + "/preview");
        
        return response;
    }
    
    /**
     * Validate upload request to ensure required fields are provided
     */
    private void validateUploadRequest(FileUploadRequest request) {
        if (request.getDepartmentId() == null) {
            throw new RuntimeException("Department is required. Please select a department.");
        }
        
        if (request.getFileTypeId() == null) {
            throw new RuntimeException("File type is required. Please select a file type.");
        }
        
        // Validate that visibility is a valid value
        if (request.getVisibility() != null) {
            String visibility = request.getVisibility().toUpperCase();
            if (!visibility.equals("PRIVATE") && !visibility.equals("DEPARTMENT") && !visibility.equals("PUBLIC")) {
                throw new RuntimeException("Invalid visibility value. Only PRIVATE, DEPARTMENT, or PUBLIC are accepted.");
            }
        }
    }
}
