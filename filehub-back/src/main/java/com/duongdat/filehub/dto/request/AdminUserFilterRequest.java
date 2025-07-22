package com.duongdat.filehub.dto.request;

import com.duongdat.filehub.entity.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserFilterRequest {
    private String username;
    private String email;
    private Role role;
    private Boolean isActive;
    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}
