package com.duongdat.filehub.controller;

import com.duongdat.filehub.service.impl.GoogleDriveTokenRefreshService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for managing Google Drive OAuth2 tokens
 * Only available when Google Drive is enabled
 */
@RestController
@RequestMapping("/api/admin/google-drive/oauth")
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "google.drive.enabled", havingValue = "true")
@PreAuthorize("hasRole('ADMIN')")
public class GoogleDriveOAuthController {

    private final GoogleDriveTokenRefreshService tokenRefreshService;

    /**
     * Force refresh the OAuth2 token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshToken() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            tokenRefreshService.forceTokenRefresh();
            
            response.put("success", true);
            response.put("message", "OAuth2 token refreshed successfully");
            log.info("OAuth2 token refresh initiated via admin endpoint");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to refresh OAuth2 token via admin endpoint: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "Failed to refresh token: " + e.getMessage());
            response.put("suggestion", "Try clearing tokens and re-authorizing");
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Clear stored OAuth2 tokens to force re-authorization
     */
    @PostMapping("/clear-tokens")
    public ResponseEntity<Map<String, Object>> clearTokens() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            tokenRefreshService.clearStoredTokens();
            
            response.put("success", true);
            response.put("message", "OAuth2 tokens cleared successfully");
            response.put("instruction", "Restart the application to re-authorize Google Drive access");
            log.info("OAuth2 tokens cleared via admin endpoint");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to clear OAuth2 tokens: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "Failed to clear tokens: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get OAuth2 token status information
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getTokenStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Try to get credential and check validity
            var credential = tokenRefreshService.getCredentialWithRefresh();
            boolean hasRefreshToken = credential.getRefreshToken() != null;
            Long expiresInSeconds = credential.getExpiresInSeconds();
            
            response.put("success", true);
            response.put("hasRefreshToken", hasRefreshToken);
            response.put("expiresInSeconds", expiresInSeconds);
            response.put("isValid", tokenRefreshService.isCredentialValid(credential));
            
            if (!hasRefreshToken) {
                response.put("warning", "No refresh token available. Token expiration may require manual re-authorization.");
                response.put("suggestion", "Revoke access at https://myaccount.google.com/permissions and re-authorize");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get OAuth2 token status: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "Failed to check token status: " + e.getMessage());
            response.put("suggestion", "OAuth2 may not be properly configured or tokens may be invalid");
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test Google Drive connection
     */
    @PostMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var credential = tokenRefreshService.getCredentialWithRefresh();
            
            // Try to create a minimal Drive service and test it
            var httpTransport = com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport();
            var jsonFactory = com.google.api.client.json.gson.GsonFactory.getDefaultInstance();
            
            var driveService = new com.google.api.services.drive.Drive.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName("FileHub-Test")
                    .build();
            
            // Test by getting about information
            var about = driveService.about().get().setFields("user").execute();
            
            response.put("success", true);
            response.put("message", "Google Drive connection successful");
            response.put("userEmail", about.getUser().getEmailAddress());
            log.info("Google Drive connection test successful");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Google Drive connection test failed: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "Connection test failed: " + e.getMessage());
            
            if (e.getMessage().contains("invalid_grant")) {
                response.put("suggestion", "Token has expired. Use /clear-tokens endpoint and restart application");
            }
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}
