package com.duongdat.filehub.dto.response;

import lombok.Data;

@Data
public class GoogleDriveUploadResponse {
    private int status;
    private String message;
    private String url;
    private String fileId;
    
    public GoogleDriveUploadResponse() {}
    
    public GoogleDriveUploadResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
    
    public GoogleDriveUploadResponse(int status, String message, String url, String fileId) {
        this.status = status;
        this.message = message;
        this.url = url;
        this.fileId = fileId;
    }
    
    public static GoogleDriveUploadResponse success(String message, String url, String fileId) {
        return new GoogleDriveUploadResponse(200, message, url, fileId);
    }
    
    public static GoogleDriveUploadResponse error(String message) {
        return new GoogleDriveUploadResponse(500, message);
    }
}
