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
    private String tags;
    private String visibility;
    private Long downloadCount;
    private Integer version;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;
    private Long categoryId;
    private String categoryName;
    private String driveFileId;
    private String driveFolderId;
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
