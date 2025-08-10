@echo off
echo Google Drive OAuth2 Token Reset Script
echo =====================================
echo.

echo Clearing expired tokens...
if exist "tokens\StoredCredential" (
    del /f "tokens\StoredCredential"
    echo Tokens cleared successfully.
) else (
    echo No tokens found to clear.
)

echo.
echo Next steps:
echo 1. Start your FileHub backend application
echo 2. When you first upload a file, a browser will open
echo 3. Sign in to your Google account and grant permissions
echo 4. The new token will be automatically saved
echo.

echo If you want to start the backend now, uncomment the line below:
REM .\gradlew bootRun

pause
