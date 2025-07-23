package com.duongdat.filehub.service.impl;

import com.duongdat.filehub.service.GoogleDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class GoogleDriveServiceImpl implements GoogleDriveService {
    
    @Override
    public String uploadFile(MultipartFile file, String filename) throws Exception {
        // TODO: Implement Google Drive upload when service account is configured
        throw new RuntimeException("Google Drive service not configured");
    }
    
    @Override
    public byte[] downloadFile(String fileId) throws Exception {
        // TODO: Implement Google Drive download when service account is configured
        throw new RuntimeException("Google Drive service not configured");
    }
    
    @Override
    public boolean deleteFile(String fileId) throws Exception {
        // TODO: Implement Google Drive delete when service account is configured
        throw new RuntimeException("Google Drive service not configured");
    }
    
    @Override
    public String createFolder(String folderName, String parentFolderId) throws Exception {
        // TODO: Implement Google Drive folder creation when service account is configured
        throw new RuntimeException("Google Drive service not configured");
    }
}
