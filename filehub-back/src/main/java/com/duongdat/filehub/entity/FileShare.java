package com.duongdat.filehub.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_shares", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"file_id", "shared_by", "shared_with"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileShare {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "file_id", nullable = false)
    private Long fileId;
    
    @Column(name = "shared_by", nullable = false)
    private Long sharedBy; // User who shares the file
    
    @Column(name = "shared_with", nullable = false)
    private Long sharedWith; // User who receives the share
    
    @Column(name = "permissions", length = 20)
    private String permissions = "read"; // 'read', 'write'
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // Optional expiration date
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Relationship mappings
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    private File file;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_by", insertable = false, updatable = false)
    private User sharedByUser;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_with", insertable = false, updatable = false)
    private User sharedWithUser;
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
