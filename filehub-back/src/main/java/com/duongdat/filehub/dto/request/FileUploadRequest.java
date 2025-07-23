package com.duongdat.filehub.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FileUploadRequest {
    
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private Long categoryId;
    
    private String tags;
    
    private String visibility = "PRIVATE"; // PRIVATE, PUBLIC, SHARED
}
