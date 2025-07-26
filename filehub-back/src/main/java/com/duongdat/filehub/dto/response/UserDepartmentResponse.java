package com.duongdat.filehub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDepartmentResponse {
    private Long id;
    private Long userId;
    private Long departmentId;
    private String departmentName;
    private String role;
    private Boolean isActive;
    private LocalDateTime assignedAt;
    private Long assignedBy;
    private String assignedByName;
}
