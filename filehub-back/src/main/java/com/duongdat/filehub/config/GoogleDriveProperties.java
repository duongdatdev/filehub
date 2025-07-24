package com.duongdat.filehub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "google.drive")
public class GoogleDriveProperties {
    
    private boolean enabled = false;
    private boolean useReal = false;
    private String serviceAccountKeyPath = "src/main/resources/google-drive-service-account.json";
    private String applicationName = "FileHub";
    private String folderName = "FileHub-Storage";
    
    // Getters and setters
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isUseReal() {
        return useReal;
    }
    
    public void setUseReal(boolean useReal) {
        this.useReal = useReal;
    }
    
    public String getServiceAccountKeyPath() {
        return serviceAccountKeyPath;
    }
    
    public void setServiceAccountKeyPath(String serviceAccountKeyPath) {
        this.serviceAccountKeyPath = serviceAccountKeyPath;
    }
    
    public String getApplicationName() {
        return applicationName;
    }
    
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
    
    public String getFolderName() {
        return folderName;
    }
    
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    
    // Additional property for explicit folder ID (like in your original code)
    private String folderId = "1eW70gnMcvPmJJzaPVtSg3CjUSZeK2x21";
    
    public String getFolderId() {
        return folderId;
    }
    
    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }
}
