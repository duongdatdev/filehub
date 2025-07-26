package com.duongdat.filehub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchUserAssignmentRequest {
    private List<Long> userIds;
    private Long departmentId;
    private Long projectId;
    private String role;
    private String operation; // "ADD", "REMOVE", "UPDATE_ROLE"
}
