package com.duongdat.filehub.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class File {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;
    
    @Column(name = "stored_filename", unique = true, nullable = false)
    private String storedFilename;
    
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;
    
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;
    
    @Column(name = "file_hash", unique = true, nullable = false, length = 64)
    private String fileHash;
    
    @Column(name = "category_id")
    private Long categoryId;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "tags", columnDefinition = "JSON")
    private String tags;
    
    @Column(name = "visibility", length = 20)
    private String visibility = "PRIVATE";
    
    @Column(name = "download_count")
    private Long downloadCount = 0L;
    
    @Column(name = "version")
    private Integer version = 1;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Google Drive specific fields
    @Column(name = "drive_file_id")
    private String driveFileId;
    
    @Column(name = "drive_folder_id")
    private String driveFolderId;
    
    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
