package com.duongdat.filehub.service;

import org.springframework.web.multipart.MultipartFile;

public interface GoogleDriveService {
    
    String uploadFile(MultipartFile file, String filename) throws Exception;
    
    byte[] downloadFile(String fileId) throws Exception;
    
    boolean deleteFile(String fileId) throws Exception;
    
    String createFolder(String folderName, String parentFolderId) throws Exception;
}
