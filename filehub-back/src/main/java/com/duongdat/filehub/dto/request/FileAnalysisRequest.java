package com.duongdat.filehub.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileAnalysisRequest {
    private String fileName;
    private String fileContent;
    private String contentType;
    private Long departmentId;
    private Long projectId;
    private String description; // For large files that can't be read
    private Long fileSize; // File size in bytes
    private String title; // File title for analysis
}
