package com.duongdat.filehub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    
    // Core classifications
    @Column(name = "uploader_id", nullable = false)
    private Long uploaderId; // Person who uploaded the file
    
    @Column(name = "department_id", nullable = false)
    private Long departmentId; // Department that owns the file
    
    @Column(name = "department_category_id")
    private Long departmentCategoryId; // Internal department classification
    
    @Column(name = "file_type_id", nullable = false)
    private Long fileTypeId; // File type (Document, Image, Video, etc.)
    
    @Column(name = "project_id")
    private Long projectId; // Project (if applicable)
    
    
    // File metadata
    @Column(name = "title")
    private String title; // Custom title
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "tags", columnDefinition = "JSON")
    private String tags; // Tags for search
    
    // Sharing permissions
    @Column(name = "visibility", length = 20)
    private String visibility = "PRIVATE"; // 'PRIVATE', 'DEPARTMENT', 'PUBLIC'
    
    // System fields
    @Column(name = "download_count")
    private Long downloadCount = 0L;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false; // Soft delete
    
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Google Drive integration (optional)
    @Column(name = "drive_file_id")
    private String driveFileId; // Google Drive file ID
    
    @Column(name = "drive_folder_id")
    private String driveFolderId; // Google Drive folder ID
    
    // Relationship mappings
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", insertable = false, updatable = false)
    @JsonIgnore
    private User uploader;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    @JsonIgnore
    private Department department;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_category_id", insertable = false, updatable = false)
    @JsonIgnore
    private DepartmentCategory departmentCategory;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_type_id", insertable = false, updatable = false)
    @JsonIgnore
    private FileType fileType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    @JsonIgnore
    private Project project;
    
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
