package com.duongdat.filehub.config;

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
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileReader;
import java.util.Collections;

/**
 * Configuration class for Google Drive OAuth2 authentication
 */
@Configuration
@ConditionalOnProperty(name = "google.drive.enabled", havingValue = "true")
@Slf4j
public class GoogleDriveOAuth2Config {

    @Value("${google.drive.oauth2.client.secrets.path:src/main/resources/credentials.json}")
    private String clientSecretsPath;

    @Value("${google.drive.oauth2.tokens.directory:tokens}")
    private String tokensDirectoryPath;

    @Value("${google.drive.application.name:FileHub}")
    private String applicationName;

    @Value("${google.drive.oauth2.port:8888}")
    private int authorizationPort;

    /**
     * Creates and configures Google Drive service with OAuth2 authentication
     */
    @Bean
    @ConditionalOnProperty(name = "google.drive.use.real", havingValue = "true")
    public Drive googleDriveService() throws Exception {
        try {
            log.info("Configuring Google Drive service with OAuth2...");
            
            Credential credential = getOAuth2Credential();
            
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            
            Drive driveService = new Drive.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(applicationName)
                    .build();
            
            log.info("Google Drive service configured successfully with OAuth2");
            return driveService;
            
        } catch (Exception e) {
            log.error("Failed to configure Google Drive service with OAuth2: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get OAuth2 credential for Google Drive API access
     */
    private Credential getOAuth2Credential() throws Exception {
        log.debug("Loading OAuth2 credentials from: {}", clientSecretsPath);
        
        // Load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                GsonFactory.getDefaultInstance(),
                new FileReader(clientSecretsPath));

        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter")) {
            throw new IllegalArgumentException(
                    "Please update the credentials.json file with your actual OAuth2 credentials. " +
                    "See GOOGLE_DRIVE_OAUTH2_SETUP.md for instructions.");
        }

        // Build flow and trigger user authorization request
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, 
                jsonFactory, 
                clientSecrets, 
                Collections.singletonList(DriveScopes.DRIVE))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensDirectoryPath)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(authorizationPort)
                .build();
                
        log.info("Starting OAuth2 authorization flow on port {}", authorizationPort);
        log.info("If this is the first time running, a browser window will open for authorization");
        
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
