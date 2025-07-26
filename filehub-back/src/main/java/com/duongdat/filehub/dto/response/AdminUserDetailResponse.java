package com.duongdat.filehub.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDetailResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<UserDepartmentSummary> departments;
    private List<UserProjectSummary> projects;
    
    // Constructor from UserResponse
    public AdminUserDetailResponse(UserResponse userResponse, List<UserDepartmentSummary> departments, List<UserProjectSummary> projects) {
        this.id = userResponse.getId();
        this.username = userResponse.getUsername();
        this.email = userResponse.getEmail();
        this.fullName = userResponse.getFullName();
        this.role = userResponse.getRole();
        this.isActive = userResponse.getIsActive();
        this.createdAt = userResponse.getCreatedAt();
        this.departments = departments;
        this.projects = projects;
    }
}
