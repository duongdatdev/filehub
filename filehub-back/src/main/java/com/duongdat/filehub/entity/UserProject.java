package com.duongdat.filehub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProject {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "project_id", nullable = false)
    private Long projectId;
    
    @Column(name = "role", length = 50)
    private String role = "MEMBER"; // 'MEMBER', 'LEAD', 'COLLABORATOR'
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    @Column(name = "assigned_by")
    private Long assignedBy; // Who assigned this user to project
    
    // Relationship mappings - using LAZY fetch to avoid circular dependencies
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Project project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User assignedByUser;
    
    // Constructors for convenience
    public UserProject(Long userId, Long projectId, String role) {
        this.userId = userId;
        this.projectId = projectId;
        this.role = role;
        this.isActive = true;
        this.assignedAt = LocalDateTime.now();
    }
    
    @PrePersist
    public void prePersist() {
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
    }
}
