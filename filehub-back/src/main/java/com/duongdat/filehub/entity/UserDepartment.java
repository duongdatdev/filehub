package com.duongdat.filehub.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDepartment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "department_id", nullable = false)
    private Long departmentId;
    
    @Column(name = "role", length = 50)
    private String role = "MEMBER"; // 'MEMBER', 'MANAGER', 'DEPUTY_MANAGER'
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    @Column(name = "assigned_by")
    private Long assignedBy; // Who assigned this user to department
    
    // Relationship mappings - using LAZY fetch to avoid circular dependencies
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    private Department department;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", insertable = false, updatable = false)
    private User assignedByUser;
    
    // Constructors for convenience
    public UserDepartment(Long userId, Long departmentId, String role) {
        this.userId = userId;
        this.departmentId = departmentId;
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
