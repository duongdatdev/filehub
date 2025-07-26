package com.duongdat.filehub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAssignmentRequest {
    private Long userId;
    private Long departmentId;
    private List<Long> projectIds;
}
