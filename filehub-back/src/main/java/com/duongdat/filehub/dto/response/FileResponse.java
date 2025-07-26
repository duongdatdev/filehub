package com.duongdat.filehub.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    
    private Long id;
    private String originalFilename;
    private String title;
    private String description;
    private Long fileSize;
    private String contentType;
    private String fileHash;
    
    // Core classifications  
    private Long uploaderId;
    private String uploaderName;
    private Long departmentId;
    private String departmentName;
    private Long departmentCategoryId;
    private String departmentCategoryName;
    private Long fileTypeId;
    private String fileTypeName;
    private Long projectId;
    private String projectName;
    
    // File metadata
    private String tags;
    private String visibility;
    private Long downloadCount;
    private Boolean isDeleted;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    
    // Google Drive integration
    private String driveFileId;
    private String driveFolderId;
    
    // URLs for download/preview
    private String downloadUrl;
    private String previewUrl;
    
    // Constructor for basic file info
    public FileResponse(Long id, String originalFilename, String title, Long fileSize, 
                       String contentType, LocalDateTime uploadedAt) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.title = title;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.uploadedAt = uploadedAt;
    }
}
