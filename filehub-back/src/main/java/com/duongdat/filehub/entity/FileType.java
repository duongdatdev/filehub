package com.duongdat.filehub.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name; // 'DOCUMENT', 'IMAGE', 'VIDEO', 'SLIDE', 'SOURCE_CODE', 'OTHER'
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "allowed_extensions", columnDefinition = "JSON")
    private String allowedExtensions; // JSON array of allowed file extensions
    
    @Column(name = "color", length = 7)
    private String color; // HEX color code
    
    @Column(name = "icon", length = 50)
    private String icon; // Icon class name
    
    @Column(name = "max_size")
    private Long maxSize; // Max file size in bytes for this type
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
