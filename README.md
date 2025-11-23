# Modul 9 PAPB - CameraX Application

This is a complete Android application implementing CameraX library for camera functionality, developed as part of Module 9 for Mobile Application Development (PAPB - Pengembangan Aplikasi Perangkat Bergerak).

## Features

- **Camera Preview**: Real-time camera preview using CameraX
- **Photo Capture**: Take photos and save them to device gallery
- **Video Recording**: Record videos with audio and save to device storage
- **Permission Handling**: Proper runtime permission requests for Camera, Audio, and Storage
- **Material Design UI**: Clean and intuitive user interface

## Technologies Used

- **Language**: Kotlin
- **Architecture**: Android Jetpack
- **Camera API**: CameraX Library
- **UI**: Material Components
- **View Binding**: Enabled for type-safe view access
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Key Components

### CameraX Dependencies
```kotlin
androidx.camera:camera-core
androidx.camera:camera-camera2
androidx.camera:camera-lifecycle
androidx.camera:camera-video
androidx.camera:camera-view
androidx.camera:camera-extensions
```

### Main Features Implementation

1. **Image Capture**
   - Uses `ImageCapture` use case
   - Saves images to MediaStore with timestamps
   - Automatic notification on successful capture

2. **Video Recording**
   - Uses `VideoCapture` with `Recorder`
   - Quality selector with fallback strategy
   - Audio recording support
   - Start/Stop toggle functionality

3. **Permissions**
   - Camera permission
   - Audio recording permission
   - Storage permissions (for Android 9 and below)

## Project Structure

```
app/
├── src/main/
│   ├── java/com/example/cameraxapp/
│   │   └── MainActivity.kt          # Main activity with camera logic
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml    # UI layout with PreviewView
│   │   ├── values/
│   │   │   ├── strings.xml          # String resources
│   │   │   ├── colors.xml           # Color definitions
│   │   │   └── themes.xml           # App theme
│   │   └── xml/
│   │       ├── backup_rules.xml     # Backup configuration
│   │       └── data_extraction_rules.xml
│   └── AndroidManifest.xml          # App manifest with permissions
└── build.gradle.kts                 # App-level build configuration
```

## How to Build

1. Clone the repository
2. Open the project in Android Studio (Hedgehog or later recommended)
3. Sync Gradle files
4. Run the app on a physical device (emulator may have limited camera support)

## Permissions Required

The app requires the following permissions:
- `CAMERA` - To access the camera hardware
- `RECORD_AUDIO` - To record audio for video capture
- `WRITE_EXTERNAL_STORAGE` - For Android 9 and below
- `READ_EXTERNAL_STORAGE` - For Android 12L and below

## Usage

1. Launch the app
2. Grant camera and storage permissions when prompted
3. Use "Take Photo" button to capture images
4. Use "Start Capture" button to begin video recording
5. Use "Stop Capture" button to stop video recording
6. Captured media is saved to device gallery automatically

## Learning Outcomes

This module demonstrates:
- Integration of CameraX library in Android applications
- Handling runtime permissions in modern Android
- Working with MediaStore API
- Implementing camera preview and capture functionality
- Managing app lifecycle with camera resources
- Using Material Design components

## Author

Module 9 - PAPB (Mobile Application Development)

## License

Educational Project - For Learning Purposes