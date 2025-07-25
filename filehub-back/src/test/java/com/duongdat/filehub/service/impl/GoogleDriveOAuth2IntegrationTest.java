package com.duongdat.filehub.service.impl;

import com.duongdat.filehub.service.GoogleDriveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Test class for Google Drive OAuth2 integration
 * Note: These tests require proper OAuth2 setup and should be run manually
 */
@SpringBootTest
@TestPropertySource(properties = {
    "google.drive.enabled=true",
    "google.drive.use.real=false" // Use simulation for testing
})
public class GoogleDriveOAuth2IntegrationTest {

    @Autowired
    private GoogleDriveService googleDriveService;

    @Test
    public void testGoogleDriveServiceInitialization() {
        // This test verifies that the service can be initialized
        // In simulation mode, it should work without OAuth2 setup
        System.out.println("Google Drive service initialized successfully");
        assert googleDriveService != null;
    }

    // Manual test for OAuth2 flow - uncomment and run manually with real=true
    /*
    @Test
    @TestPropertySource(properties = {
        "google.drive.enabled=true",
        "google.drive.use.real=true"
    })
    public void testOAuth2Flow() throws Exception {
        // This test will trigger the OAuth2 flow
        // Run this manually to test the OAuth2 setup
        String folderId = googleDriveService.createFolder("Test-OAuth2-Folder", null);
        log.info("Created test folder with ID: {}", folderId);
        
        // Clean up
        googleDriveService.deleteFile(folderId);
    }
    */
}
