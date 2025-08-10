# Google Drive OAuth2 Token Expired - Troubleshooting Guide

## Problem Description

The error `"invalid_grant", "error_description": "Token has been expired or revoked."` indicates that your Google Drive OAuth2 token has expired and needs to be refreshed or re-authorized.

## Root Cause

OAuth2 tokens have a limited lifespan:
- **Access tokens**: Usually expire after 1 hour
- **Refresh tokens**: Can expire if not used for 6 months, or if revoked

## Solutions (Choose One)

### Solution 1: Quick Fix - Clear Tokens and Re-authorize

1. **Stop your application** if it's running
2. **Clear the expired tokens**:
   ```bash
   # Delete the tokens directory content
   rm -rf filehub-back/tokens/StoredCredential
   # Or run the provided batch script
   ./filehub-back/refresh-google-drive-auth.bat
   ```
3. **Restart your application**:
   ```bash
   cd filehub-back
   ./gradlew bootRun
   ```
4. **Trigger OAuth2 flow**:
   - Try to upload a file through your application
   - A browser window will open automatically
   - Sign in to your Google account
   - Grant permissions to FileHub
   - The new token will be saved automatically

### Solution 2: Use the New Admin API Endpoints

I've created new admin endpoints to manage OAuth2 tokens:

1. **Check token status**:
   ```bash
   GET /api/admin/google-drive/oauth/status
   ```

2. **Clear tokens manually**:
   ```bash
   POST /api/admin/google-drive/oauth/clear-tokens
   ```

3. **Test Google Drive connection**:
   ```bash
   POST /api/admin/google-drive/oauth/test-connection
   ```

### Solution 3: Automatic Token Refresh (Implemented)

The application now includes automatic token refresh:
- Tokens are checked before each API call
- Expired tokens are automatically refreshed if a refresh token is available
- If refresh fails, the user is prompted to re-authorize

## Code Changes Made

### 1. New Token Refresh Service
- `GoogleDriveTokenRefreshService.java` - Handles token validation and refresh
- Automatic token refresh before API calls
- Better error handling for expired tokens

### 2. Enhanced Google Drive Service
- Updated `GoogleDriveServiceRealImpl.java` to use token refresh service
- Added credential validation before uploads
- Better error messages for token-related issues

### 3. Admin Management Endpoints
- `GoogleDriveOAuthController.java` - Admin endpoints for token management
- Check token status
- Force token refresh
- Clear tokens
- Test connection

## Configuration Updates

Make sure your `application.properties` includes:

```properties
# Google Drive OAuth2 Configuration
google.drive.enabled=true
google.drive.use.real=true
google.drive.oauth2.client.secrets.path=src/main/resources/credentials.json
google.drive.oauth2.tokens.directory=tokens
google.drive.application.name=FileHub
google.drive.oauth2.port=8888
```

## Prevention Tips

1. **Monitor token expiration**: Use the status endpoint regularly
2. **Handle refresh tokens properly**: Ensure `setAccessType("offline")` is set
3. **Regular testing**: Use the test connection endpoint periodically
4. **Backup strategy**: Consider implementing a fallback to simulation mode

## Troubleshooting Steps

If you still have issues:

1. **Check credentials.json**:
   - Ensure the file exists and is valid
   - Verify client_id and client_secret are correct

2. **Check Google Cloud Console**:
   - Ensure Google Drive API is enabled
   - Check OAuth2 client configuration
   - Verify redirect URIs include `http://localhost`

3. **Network/Firewall**:
   - Ensure port 8888 is available for OAuth2 callback
   - Check firewall settings

4. **Browser cache**:
   - Clear browser cache and cookies for Google accounts
   - Try using an incognito/private browsing window

## Testing the Fix

After implementing the solution:

1. **Start the backend**:
   ```bash
   cd filehub-back
   ./gradlew bootRun
   ```

2. **Test with a file upload**:
   - Use your frontend or curl to upload a file
   - Check logs for successful OAuth2 flow

3. **Monitor logs**:
   ```bash
   # Look for these success messages:
   # "OAuth2 credential loaded successfully with refresh token"
   # "File uploaded to real Google Drive: filename -> fileId"
   ```

## Emergency Fallback

If OAuth2 continues to fail, you can temporarily switch to simulation mode:

```properties
google.drive.use.real=false
```

This will use local file storage instead of Google Drive while you resolve the OAuth2 issues.

## Support

If you need further assistance:
1. Check the application logs for detailed error messages
2. Use the admin endpoints to diagnose token issues
3. Verify your Google Cloud Console configuration
4. Consider regenerating OAuth2 credentials if necessary
