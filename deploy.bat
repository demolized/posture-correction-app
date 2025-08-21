@echo off
chcp 65001 >nul
echo 🚀 Posture Correction App - Complete Deployment Script
echo ======================================================
echo.

echo [INFO] Starting complete deployment process...
echo.

REM Step 1: Install dependencies
echo [INFO] Step 1: Installing dependencies...
call npm install
if %errorlevel% neq 0 (
    echo [ERROR] Failed to install dependencies
    pause
    exit /b 1
)
echo [SUCCESS] Dependencies installed successfully
echo.

REM Step 2: Build the web app
echo [INFO] Step 2: Building web application...
call npm run build
if %errorlevel% neq 0 (
    echo [ERROR] Failed to build web app
    pause
    exit /b 1
)
echo [SUCCESS] Web app built successfully
echo.

REM Step 3: Sync with Android
echo [INFO] Step 3: Syncing with Android platform...
call npx cap sync android
if %errorlevel% neq 0 (
    echo [ERROR] Failed to sync Android platform
    echo [WARNING] Trying to add Android platform...
    call npx cap add android
    if %errorlevel% neq 0 (
        echo [ERROR] Failed to add Android platform
        pause
        exit /b 1
    )
    echo [SUCCESS] Android platform added successfully
    call npx cap sync android
    if %errorlevel% neq 0 (
        echo [ERROR] Failed to sync Android platform after adding
        pause
        exit /b 1
    )
)
echo [SUCCESS] Android platform synced successfully
echo.

REM Step 4: Build Android APK
echo [INFO] Step 4: Building Android APK...
call npm run build:apk
if %errorlevel% neq 0 (
    echo [WARNING] APK build failed, trying alternative method...
    cd android
    call gradlew.bat assembleDebug
    if %errorlevel% neq 0 (
        echo [ERROR] Failed to build Android APK
        cd ..
        pause
        exit /b 1
    )
    echo [SUCCESS] Android APK built successfully via Gradle
    cd ..
)
echo [SUCCESS] Android APK built successfully
echo.

REM Step 5: Deploy to GitHub Pages
echo [INFO] Step 5: Deploying to GitHub Pages...
call npm run deploy
if %errorlevel% neq 0 (
    echo [WARNING] GitHub Pages deployment failed
    echo [INFO] You can deploy manually later with: npm run deploy
)
echo.

REM Step 6: Check APK location
echo [INFO] Step 6: Checking APK location...
set APK_PATH=android\app\build\outputs\apk\debug\app-debug.apk
if exist "%APK_PATH%" (
    echo [SUCCESS] APK found at: %APK_PATH%
    for %%A in ("%APK_PATH%") do echo [SUCCESS] APK size: %%~zA bytes
) else (
    echo [ERROR] APK not found at expected location
    echo [INFO] Searching for APK files...
    dir /s /b android\*.apk
)
echo.

REM Step 7: Summary
echo 🎉 Deployment Summary
echo ====================
echo ✅ Web App: Built and ready for deployment
echo ✅ Android APK: Built and ready for distribution
echo.
echo 📱 Next Steps:
echo 1. Push your code to GitHub:
echo    git add . ^&^& git commit -m "Complete deployment setup" ^&^& git push origin main
echo.
echo 2. Create a GitHub Release:
echo    - Go to your repository on GitHub
echo    - Click 'Releases' → 'Create a new release'
echo    - Upload the APK from: %APK_PATH%
echo    - Tag: v1.0.0
echo    - Title: Posture Correction App v1.0.0
echo.
echo 3. Configure GitHub Pages:
echo    - Go to Settings → Pages
echo    - Source: Deploy from a branch
echo    - Branch: gh-pages / /(root)
echo.
echo 🌐 Your app will be available at:
echo    Web: https://demolized.github.io/posture-correction-app
echo    APK: https://github.com/demolized/posture-correction-app/releases/latest
echo.
echo [SUCCESS] Deployment script completed successfully!
echo.
pause
