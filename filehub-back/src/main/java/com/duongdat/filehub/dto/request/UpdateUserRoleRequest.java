package com.duongdat.filehub.dto.request;

import com.duongdat.filehub.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRoleRequest {
    
    @NotNull(message = "role field is required")
    private Role role;
}
