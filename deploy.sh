#!/bin/bash

echo "🚀 Posture Correction App - Complete Deployment Script"
echo "======================================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if we're in the right directory
if [ ! -f "package.json" ]; then
    print_error "Please run this script from the project root directory"
    exit 1
fi

print_status "Starting complete deployment process..."

# Step 1: Install dependencies
print_status "Step 1: Installing dependencies..."
if npm install; then
    print_success "Dependencies installed successfully"
else
    print_error "Failed to install dependencies"
    exit 1
fi

# Step 2: Build the web app
print_status "Step 2: Building web application..."
if npm run build; then
    print_success "Web app built successfully"
else
    print_error "Failed to build web app"
    exit 1
fi

# Step 3: Sync with Android
print_status "Step 3: Syncing with Android platform..."
if npx cap sync android; then
    print_success "Android platform synced successfully"
else
    print_error "Failed to sync Android platform"
    print_warning "Trying to add Android platform..."
    if npx cap add android; then
        print_success "Android platform added successfully"
        if npx cap sync android; then
            print_success "Android platform synced successfully"
        else
            print_error "Failed to sync Android platform after adding"
            exit 1
        fi
    else
        print_error "Failed to add Android platform"
        exit 1
    fi
fi

# Step 4: Build Android APK
print_status "Step 4: Building Android APK..."
if npm run build:apk; then
    print_success "Android APK built successfully"
else
    print_warning "APK build failed, trying alternative method..."
    cd android
    if ./gradlew assembleDebug; then
        print_success "Android APK built successfully via Gradle"
        cd ..
    else
        print_error "Failed to build Android APK"
        cd ..
        exit 1
    fi
fi

# Step 5: Deploy to GitHub Pages
print_status "Step 5: Deploying to GitHub Pages..."
if npm run deploy; then
    print_success "Web app deployed to GitHub Pages successfully"
else
    print_warning "GitHub Pages deployment failed"
    print_status "You can deploy manually later with: npm run deploy"
fi

# Step 6: Check APK location
print_status "Step 6: Checking APK location..."
APK_PATH="android/app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    print_success "APK found at: $APK_PATH"
    print_success "APK size: $APK_SIZE"
else
    print_error "APK not found at expected location"
    print_status "Searching for APK files..."
    find android -name "*.apk" -type f
fi

# Step 7: Summary
echo ""
echo "🎉 Deployment Summary"
echo "===================="
echo -e "${GREEN}✅ Web App${NC}: Built and ready for deployment"
echo -e "${GREEN}✅ Android APK${NC}: Built and ready for distribution"
echo ""
echo "📱 Next Steps:"
echo "1. Push your code to GitHub:"
echo "   git add . && git commit -m 'Complete deployment setup' && git push origin main"
echo ""
echo "2. Create a GitHub Release:"
echo "   - Go to your repository on GitHub"
echo "   - Click 'Releases' → 'Create a new release'"
echo "   - Upload the APK from: $APK_PATH"
echo "   - Tag: v1.0.0"
echo "   - Title: Posture Correction App v1.0.0"
echo ""
echo "3. Configure GitHub Pages:"
echo "   - Go to Settings → Pages"
echo "   - Source: Deploy from a branch"
echo "   - Branch: gh-pages / /(root)"
echo ""
echo "🌐 Your app will be available at:"
echo "   Web: https://demolized.github.io/posture-correction-app"
echo "   APK: https://github.com/demolized/posture-correction-app/releases/latest"
echo ""
print_success "Deployment script completed successfully!"
