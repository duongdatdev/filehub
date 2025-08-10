package com.duongdat.filehub.service.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;

/**
 * Service to handle Google Drive OAuth2 token refresh and credential management
 */
@Service
@Slf4j
public class GoogleDriveTokenRefreshService {

    @Value("${google.drive.oauth2.client.secrets.path:src/main/resources/credentials.json}")
    private String clientSecretsPath;

    @Value("${google.drive.oauth2.tokens.directory:tokens}")
    private String tokensDirectoryPath;

    @Value("${google.drive.oauth2.port:8888}")
    private int authorizationPort;

    /**
     * Get OAuth2 credential with automatic token refresh capability
     */
    public Credential getCredentialWithRefresh() throws Exception {
        try {
            log.info("Loading OAuth2 credentials with refresh capability...");
            
            // Load client secrets
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                    GsonFactory.getDefaultInstance(),
                    new FileReader(clientSecretsPath));

            // Build flow with automatic refresh
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, 
                    jsonFactory, 
                    clientSecrets, 
                    Collections.singletonList(DriveScopes.DRIVE))
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensDirectoryPath)))
                    .setAccessType("offline")
                    .setApprovalPrompt("force") // Force refresh token generation
                    .build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                    .setPort(authorizationPort)
                    .build();

            // Get credential with automatic refresh
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
            
            // Check if credential has refresh token
            if (credential.getRefreshToken() == null) {
                log.warn("No refresh token available. This may cause token expiration issues.");
                log.info("To get a refresh token, revoke access and re-authorize: https://myaccount.google.com/permissions");
            } else {
                log.info("OAuth2 credential loaded successfully with refresh token");
            }
            
            return credential;
            
        } catch (Exception e) {
            log.error("Failed to get OAuth2 credential: {}", e.getMessage(), e);
            throw new Exception("OAuth2 credential setup failed: " + e.getMessage());
        }
    }

    /**
     * Force refresh the current stored token
     */
    public void forceTokenRefresh() throws Exception {
        try {
            log.info("Forcing OAuth2 token refresh...");
            
            Credential credential = getCredentialWithRefresh();
            
            // Force refresh if possible
            if (credential.getRefreshToken() != null) {
                credential.refreshToken();
                log.info("Token refreshed successfully");
            } else {
                log.warn("Cannot refresh token - no refresh token available");
                throw new Exception("No refresh token available for token refresh");
            }
            
        } catch (Exception e) {
            log.error("Failed to refresh token: {}", e.getMessage(), e);
            throw new Exception("Token refresh failed: " + e.getMessage());
        }
    }

    /**
     * Check if the current credential is valid and refresh if needed
     */
    public boolean isCredentialValid(Credential credential) {
        if (credential == null) {
            return false;
        }

        try {
            // Check if token is expired
            Long expiresInSeconds = credential.getExpiresInSeconds();
            if (expiresInSeconds != null && expiresInSeconds <= 60) { // Refresh if expires within 1 minute
                log.info("Token expires in {} seconds, attempting refresh...", expiresInSeconds);
                
                if (credential.getRefreshToken() != null) {
                    credential.refreshToken();
                    log.info("Token refreshed successfully");
                    return true;
                } else {
                    log.warn("Token expired and no refresh token available");
                    return false;
                }
            }
            
            return true;
            
        } catch (IOException e) {
            log.error("Failed to refresh expired token: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Clear stored tokens to force re-authorization
     */
    public void clearStoredTokens() {
        try {
            java.io.File tokensDir = new java.io.File(tokensDirectoryPath);
            if (tokensDir.exists()) {
                java.io.File[] files = tokensDir.listFiles();
                if (files != null) {
                    for (java.io.File file : files) {
                        if (file.delete()) {
                            log.info("Deleted token file: {}", file.getName());
                        } else {
                            log.warn("Failed to delete token file: {}", file.getName());
                        }
                    }
                }
            }
            log.info("Stored tokens cleared. Next access will require re-authorization.");
            
        } catch (Exception e) {
            log.error("Failed to clear stored tokens: {}", e.getMessage(), e);
        }
    }
}
