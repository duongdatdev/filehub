package com.duongdat.filehub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityResponse {
    private Long id;
    private String type; // user, project, department, file
    private String description;
    private String user;
    private LocalDateTime timestamp;
}
