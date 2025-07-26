# Google Drive OAuth2 Setup Guide

This document explains how to set up OAuth2 authentication for Google Drive integration in the FileHub application.

## Overview

The application has been updated to use OAuth2 instead of service account authentication for Google Drive API access. This provides better security and user consent management.

## Setup Steps

### 1. Create Google Cloud Project and Enable Drive API

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google Drive API for your project
4. Go to "APIs & Services" > "Library"
5. Search for "Google Drive API" and enable it

### 2. Create OAuth2 Credentials

1. Go to "APIs & Services" > "Credentials"
2. Click "Create Credentials" > "OAuth 2.0 Client IDs"
3. Choose "Desktop application" as the application type
4. Give it a name (e.g., "FileHub Desktop Client")
5. Download the JSON file

### 3. Configure the Application

1. Rename the downloaded JSON file to `credentials.json`
2. Place it in `src/main/resources/` directory
3. Update the `application.properties` file if needed:

```properties
# Google Drive Configuration - OAuth2
google.drive.oauth2.client.secrets.path=src/main/resources/credentials.json
google.drive.oauth2.tokens.directory=tokens
google.drive.application.name=FileHub
google.drive.enabled=true
google.drive.use.real=true
```

### 4. First Run Authorization

When you first run the application with OAuth2 enabled:

1. The application will open a browser window
2. You'll be prompted to sign in to your Google account
3. Grant permission for the application to access Google Drive
4. The authorization will be saved locally for future use

### 5. Tokens Directory

The application will create a `tokens` directory to store OAuth2 tokens for automatic refresh. Make sure this directory is writable by the application.

## Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `google.drive.oauth2.client.secrets.path` | Path to the OAuth2 credentials JSON file | `src/main/resources/credentials.json` |
| `google.drive.oauth2.tokens.directory` | Directory to store OAuth2 tokens | `tokens` |
| `google.drive.application.name` | Application name for Google Drive API | `FileHub` |
| `google.drive.enabled` | Enable/disable Google Drive integration | `false` |
| `google.drive.use.real` | Use real Google Drive (true) or simulation (false) | `false` |

## Security Considerations

1. **Never commit credentials.json to version control**
2. Add `credentials.json` to your `.gitignore` file
3. The `tokens` directory contains sensitive refresh tokens - also exclude from version control
4. Each developer/deployment environment needs their own credentials.json file

## Troubleshooting

### Common Issues

1. **"File not found: credentials.json"**
   - Ensure the file exists in the correct location
   - Check the path in application.properties

2. **"Authorization failed"**
   - Check that the Google Drive API is enabled
   - Verify the OAuth2 client is configured correctly
   - Ensure the redirect URIs include `http://localhost`

3. **"Token expired"**
   - Delete the `tokens` directory and re-authorize
   - The application should automatically refresh tokens

### Fallback to Simulation

If OAuth2 setup fails, the application will automatically fall back to simulation mode using local file storage.

## Benefits of OAuth2

1. **User Consent**: Users explicitly grant permission
2. **Token Refresh**: Automatic token refresh without user intervention
3. **Revocable Access**: Users can revoke access through Google Account settings
4. **No Service Account Keys**: No need to manage service account JSON files
5. **Better Security**: Follows OAuth2 best practices
