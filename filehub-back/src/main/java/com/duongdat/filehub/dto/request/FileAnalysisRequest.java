package com.duongdat.filehub.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileAnalysisRequest {
    private String fileName;
    private String contentType;
    private Long departmentId;
    private Long projectId;
    private String description; // Additional description for analysis context
    private Long fileSize; // File size in bytes
    private String title; // File title for analysis
    private byte[] fileData; // Raw file data uploaded to Gemini Files API
}
