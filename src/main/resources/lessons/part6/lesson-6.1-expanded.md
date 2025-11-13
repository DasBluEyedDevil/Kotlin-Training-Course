# Lesson 6.1: Android Fundamentals & Setup

**Estimated Time**: 75 minutes

---

## Introduction

Welcome to **Part 6: Android Development with Kotlin**!

You've mastered Kotlin fundamentals, object-oriented programming, functional programming, and backend development with Ktor. Now it's time to bring your skills to mobile development.

Android is the world's most popular mobile platform with over **3 billion active devices**. With Jetpack Compose, Google's modern UI toolkit, building beautiful Android apps has never been easier or more intuitive.

In this lesson, you'll:
- ✅ Understand the Android platform and ecosystem
- ✅ Install and configure Android Studio (Ladybug/Otter)
- ✅ Create your first Android project
- ✅ Understand Android project structure
- ✅ Learn about the Gradle build system
- ✅ Run apps on emulator and physical devices

---

## The Android Platform

### What is Android?

**Android** is an open-source mobile operating system developed by Google, based on the Linux kernel.

**Key Features**:
- **Open Source**: Free to use and modify (AOSP - Android Open Source Project)
- **App Sandboxing**: Each app runs in its own secure environment
- **Rich Framework**: Extensive APIs for sensors, camera, location, networking, etc.
- **Google Play Store**: Primary distribution channel (3.5+ million apps)
- **Backward Compatibility**: Support for older devices

### Android Architecture

```
┌─────────────────────────────────────┐
│   System Apps                       │  Calendar, Camera, etc.
├─────────────────────────────────────┤
│   Java API Framework                │  Activity Manager, View System
├─────────────────────────────────────┤
│   Native Libraries    │   ART       │  C/C++ libs, Runtime
├─────────────────────────────────────┤
│   Hardware Abstraction Layer (HAL)  │  Camera, Bluetooth, etc.
├─────────────────────────────────────┤
│   Linux Kernel                      │  Drivers, Security
└─────────────────────────────────────┘
```

### Android Versions

| Version       | API Level | Release Year | Key Features                    |
|---------------|-----------|--------------|--------------------------------|
| **Android 15**| 35        | 2024         | Enhanced privacy, performance  |
| **Android 14**| 34        | 2023         | Improved battery, gestures     |
| **Android 13**| 33        | 2022         | Themed icons, permissions      |
| **Android 12**| 31-32     | 2021         | Material You, widgets          |
| **Android 11**| 30        | 2020         | Chat bubbles, one-time perms   |

**Minimum API Level**: We'll target **API 24 (Android 7.0)** which covers **95%+ of active devices**.

---

## Why Jetpack Compose?

### Traditional Android UI (XML)

```xml
<!-- Old way: Separate XML layout files -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <TextView
        android:id="@+id/title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello" />

    <Button
        android:id="@+id/button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Click Me" />
</LinearLayout>
```

Then in Kotlin:
```kotlin
val title = findViewById<TextView>(R.id.title)
title.text = "Hello World"
```

### Jetpack Compose (Modern)

```kotlin
@Composable
fun Greeting() {
    Column {
        Text("Hello World")
        Button(onClick = { /* handle click */ }) {
            Text("Click Me")
        }
    }
}
```

**Benefits**:
- ✅ **Less Code**: 40% less code than XML
- ✅ **Type Safety**: Compiler catches errors
- ✅ **Declarative**: Describe what UI should look like, not how to build it
- ✅ **Kotlin First**: Leverage Kotlin features (lambdas, extension functions)
- ✅ **Reactive**: UI automatically updates when state changes
- ✅ **Interoperable**: Works with existing Android Views

---

## Installing Android Studio

### Download

1. Go to [developer.android.com/studio](https://developer.android.com/studio)
2. Download **Android Studio Ladybug** or **Otter** (latest stable version)
3. Choose your platform:
   - **Windows**: `.exe` installer
   - **macOS**: `.dmg` disk image
   - **Linux**: `.tar.gz` archive

### Installation

**Windows**:
1. Run the `.exe` installer
2. Follow the setup wizard
3. Choose "Standard" installation type
4. Select UI theme (Light or Dark)
5. Wait for SDK components to download (~3 GB)

**macOS**:
1. Open the `.dmg` file
2. Drag **Android Studio** to Applications folder
3. Launch Android Studio
4. Follow setup wizard
5. Grant necessary permissions (disk access, etc.)

**Linux**:
```bash
# Extract archive
tar -xzf android-studio-*.tar.gz

# Move to /opt
sudo mv android-studio /opt/

# Run Android Studio
/opt/android-studio/bin/studio.sh
```

### First Launch

On first launch, you'll see the **Setup Wizard**:

1. **Import Settings**: Choose "Do not import settings" (first time)
2. **Data Sharing**: Choose whether to share usage data
3. **Install Type**: Select **Standard**
4. **UI Theme**: Choose **Darcula** (dark) or **Light**
5. **Verify Settings**: Review SDK location (usually `~/Android/Sdk`)
6. **License Agreements**: Accept all licenses
7. **Download Components**: Wait for SDK download (3-5 GB)

### SDK Components

The Android SDK includes:
- **SDK Platform**: Android API libraries for each version
- **SDK Tools**: Build tools, platform tools (adb, fastboot)
- **Emulator**: Virtual device for testing
- **System Images**: OS images for emulator

Check installed components:
- **Tools** → **SDK Manager**

---

## Creating Your First Android Project

### Step 1: New Project Wizard

1. Open Android Studio
2. Click **New Project**
3. Select **Empty Activity** (Compose)
4. Click **Next**

### Step 2: Configure Project

**Name**: `HelloCompose`
**Package name**: `com.example.hellocompose`
**Save location**: Choose a directory
**Language**: **Kotlin**
**Minimum SDK**: **API 24 (Android 7.0)**
**Build configuration language**: **Kotlin DSL (build.gradle.kts)**

Click **Finish** and wait for Gradle sync (~30 seconds).

### Step 3: Explore the Project

Android Studio creates a complete project structure with:
- ✅ Gradle build configuration
- ✅ Sample `MainActivity.kt`
- ✅ Composable UI functions
- ✅ Unit test setup
- ✅ AndroidManifest.xml

---

## Project Structure Explained

```
HelloCompose/
├── app/                         # Main app module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/hellocompose/
│   │   │   │   └── MainActivity.kt          # Main entry point
│   │   │   ├── res/                         # Resources
│   │   │   │   ├── drawable/                # Images, icons
│   │   │   │   ├── mipmap/                  # App launcher icons
│   │   │   │   └── values/                  # Strings, colors, themes
│   │   │   │       ├── strings.xml
│   │   │   │       ├── colors.xml
│   │   │   │       └── themes.xml
│   │   │   └── AndroidManifest.xml          # App configuration
│   │   ├── androidTest/                     # UI tests
│   │   └── test/                            # Unit tests
│   ├── build.gradle.kts                     # Module build config
│   └── proguard-rules.pro                   # Code obfuscation
├── gradle/
│   └── libs.versions.toml                   # Dependency versions
├── build.gradle.kts                         # Project build config
├── settings.gradle.kts                      # Project settings
└── gradle.properties                        # Gradle configuration
```

### Key Files

#### 1. AndroidManifest.xml

Every Android app must have a manifest file that declares:
- App components (activities, services, etc.)
- Permissions (internet, camera, etc.)
- App metadata (icon, label, theme)

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.HelloCompose"
        tools:targetApi="31">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.HelloCompose">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

**Key attributes**:
- `android:icon`: App launcher icon
- `android:label`: App name (shown in launcher)
- `android:theme`: App theme
- `android:exported="true"`: Activity can be launched by other apps
- `<intent-filter>`: Makes this the launcher activity (app entry point)

#### 2. MainActivity.kt

The main entry point of your app:

```kotlin
package com.example.hellocompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.hellocompose.ui.theme.HelloComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HelloComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HelloComposeTheme {
        Greeting("Android")
    }
}
```

**Key concepts**:
- `ComponentActivity`: Base class for Compose apps
- `onCreate()`: Called when activity is created
- `setContent {}`: Sets the Compose UI
- `@Composable`: Marks a function that emits UI
- `@Preview`: Allows previewing UI in Android Studio

#### 3. build.gradle.kts (Module Level)

Gradle configuration for the app module:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.hellocompose"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.hellocompose"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
```

**Key sections**:
- `compileSdk`: SDK version used to compile the app
- `minSdk`: Minimum Android version supported
- `targetSdk`: Version the app is tested against
- `versionCode`: Internal version number (increment for each release)
- `versionName`: User-visible version string (1.0, 1.1, 2.0, etc.)
- `dependencies`: Libraries used by the app

#### 4. gradle/libs.versions.toml

Centralized version catalog (new in Android):

```toml
[versions]
agp = "8.7.0"
kotlin = "2.0.21"
coreKtx = "1.15.0"
junit = "4.13.2"
junitVersion = "1.2.1"
espressoCore = "3.6.1"
lifecycleRuntimeKtx = "2.8.7"
activityCompose = "1.9.3"
composeBom = "2025.08.00"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-test-ext-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-test-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

**Benefits**:
- ✅ Single source of truth for versions
- ✅ Easy to update dependencies
- ✅ Type-safe accessors (`libs.androidx.core.ktx`)

---

## Understanding Gradle

### What is Gradle?

**Gradle** is a build automation tool that:
- Compiles Kotlin/Java code
- Packages resources (images, strings, layouts)
- Generates APK/AAB files
- Runs tests
- Manages dependencies

### Gradle Build Process

```
Source Code (.kt) ──┐
Resources (.xml)   ─┼──> Gradle ──> Compilation ──> DEX ──> APK/AAB
Dependencies       ──┘
```

**Steps**:
1. **Download dependencies** from Maven repositories
2. **Compile Kotlin** to JVM bytecode
3. **Convert bytecode** to DEX (Dalvik Executable) for Android
4. **Package** code, resources, and assets into APK/AAB
5. **Sign** the package with a keystore

### Gradle Tasks

Common tasks (run from Terminal in Android Studio):

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install app on connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean
```

### Sync Project

After modifying `build.gradle.kts`, click **Sync Now** or:
- **File** → **Sync Project with Gradle Files**

This downloads new dependencies and updates the project.

---

## Running Your App

### Option 1: Android Emulator

#### Create a Virtual Device

1. Click **Device Manager** (phone icon in toolbar)
2. Click **Create Device**
3. Select hardware:
   - **Phone** → **Pixel 8** (recommended)
   - Or choose any device
4. Click **Next**
5. Select system image:
   - **Release Name**: **VanillaIceCream** (API 35, Android 15)
   - Click **Download** if not installed
6. Click **Next**
7. Name: **Pixel_8_API_35**
8. Click **Finish**

#### Run on Emulator

1. Select **Pixel_8_API_35** from device dropdown
2. Click **Run** (green play button) or press **Shift + F10**
3. Wait for emulator to boot (~30 seconds first time)
4. App launches automatically

### Option 2: Physical Device

#### Enable Developer Options

**Android 12+**:
1. Open **Settings**
2. Go to **About phone**
3. Tap **Build number** 7 times
4. Enter PIN/password
5. "You are now a developer!" appears

#### Enable USB Debugging

1. Go to **Settings** → **System** → **Developer options**
2. Enable **USB debugging**

#### Connect Device

1. Connect phone to computer via USB
2. On phone, allow USB debugging prompt
3. In Android Studio, select your device from dropdown
4. Click **Run**

### Option 3: Wireless Debugging (Android 11+)

1. Connect phone and computer to same Wi-Fi
2. Enable **Wireless debugging** in Developer options
3. Click **Pair device with pairing code**
4. In Android Studio: **Pair Devices Using Wi-Fi**
5. Enter pairing code
6. Run app wirelessly

---

## Understanding the Build Output

### APK vs AAB

**APK (Android Package)**:
- Installable file for Android devices
- Contains all code, resources, and assets
- Used for direct installation and testing

**AAB (Android App Bundle)**:
- Publishing format for Google Play
- Google Play generates optimized APKs for each device
- Smaller downloads (only includes necessary resources)

### Build Locations

**Debug APK**:
```
app/build/outputs/apk/debug/app-debug.apk
```

**Release APK**:
```
app/build/outputs/apk/release/app-release-unsigned.apk
```

**AAB**:
```
app/build/outputs/bundle/release/app-release.aab
```

---

## Debugging Tools

### Logcat

View app logs in real-time:

```kotlin
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        Log.i("MainActivity", "Info message")
        Log.w("MainActivity", "Warning message")
        Log.e("MainActivity", "Error message")
    }
}
```

**Log levels**:
- `Log.v()`: Verbose (lowest priority)
- `Log.d()`: Debug
- `Log.i()`: Info
- `Log.w()`: Warning
- `Log.e()`: Error
- `Log.wtf()`: What a Terrible Failure (critical errors)

View logs in **Logcat** tab (bottom of Android Studio).

### Android Debug Bridge (adb)

Command-line tool for device communication:

```bash
# List connected devices
adb devices

# Install APK
adb install app-debug.apk

# Uninstall app
adb uninstall com.example.hellocompose

# View logs
adb logcat

# Clear logs
adb logcat -c

# Shell into device
adb shell

# Take screenshot
adb shell screencap /sdcard/screen.png
adb pull /sdcard/screen.png

# Record screen
adb shell screenrecord /sdcard/demo.mp4
```

### Layout Inspector

Inspect UI hierarchy in real-time:
1. Run app on device/emulator
2. **Tools** → **Layout Inspector**
3. Select running process
4. Explore component tree and properties

---

## Exercise 1: Customize the App

Modify your `MainActivity.kt` to:
1. Change greeting to your name
2. Add a second `Text` composable with your favorite color
3. Add log statements in `onCreate()`

### Requirements

```kotlin
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Hello $name!")
        Text(text = "My favorite color is Blue")
    }
}
```

Run the app and verify:
- Both text lines appear
- Logs appear in Logcat

---

## Solution 1

```kotlin
package com.example.hellocompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.hellocompose.ui.theme.HelloComposeTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Activity is being created")
        Log.i(TAG, "App version: 1.0")

        enableEdgeToEdge()
        setContent {
            HelloComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Alice",  // Your name here
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        Log.d(TAG, "onCreate: UI setup complete")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Hello $name!")
        Text(text = "My favorite color is Purple")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HelloComposeTheme {
        Greeting("Alice")
    }
}
```

**Logcat output**:
```
D/MainActivity: onCreate: Activity is being created
I/MainActivity: App version: 1.0
D/MainActivity: onCreate: UI setup complete
```

---

## Exercise 2: Create a Virtual Device

Create a new emulator with different specifications:

### Requirements

1. Create a tablet emulator
2. Use **Pixel Tablet** hardware
3. Install **API 34 (Android 14)** system image
4. Name it **MyTablet**
5. Run your app on this device

---

## Solution 2

**Steps**:

1. Click **Device Manager**
2. Click **Create Device**
3. Select **Tablet** category
4. Choose **Pixel Tablet**
5. Click **Next**
6. Select **UpsideDownCake** (API 34)
7. Download if necessary
8. Click **Next**
9. AVD Name: **MyTablet**
10. Click **Finish**
11. Select **MyTablet** from dropdown
12. Click **Run**

**Result**: App runs on a larger screen, text appears smaller relative to screen size.

---

## Exercise 3: Explore Project Files

Answer these questions by exploring the project:

1. What is the app's package name?
2. What is the minimum SDK version?
3. What is the Compose BOM version?
4. Where are app icons stored?
5. What is the launcher activity name?

---

## Solution 3

**Answers**:

1. **Package name**: `com.example.hellocompose`
   - Found in `build.gradle.kts` → `namespace`
   - Also in `AndroidManifest.xml` → `package`

2. **Minimum SDK**: API 24 (Android 7.0)
   - Found in `build.gradle.kts` → `defaultConfig` → `minSdk`

3. **Compose BOM version**: `2025.08.00`
   - Found in `gradle/libs.versions.toml` → `[versions]` → `composeBom`

4. **App icons**: `app/src/main/res/mipmap/`
   - `ic_launcher.png` (various densities: hdpi, mdpi, xhdpi, etc.)
   - `ic_launcher_round.png`

5. **Launcher activity**: `MainActivity`
   - Found in `AndroidManifest.xml`:
   ```xml
   <activity android:name=".MainActivity">
       <intent-filter>
           <action android:name="android.intent.action.MAIN" />
           <category android:name="android.intent.category.LAUNCHER" />
       </intent-filter>
   </activity>
   ```

---

## Checkpoint Quiz

### Question 1
What is the minimum API level that covers 95%+ of Android devices in 2025?

A) API 21 (Android 5.0)
B) API 24 (Android 7.0)
C) API 28 (Android 9.0)
D) API 31 (Android 12)

### Question 2
What is the main benefit of Jetpack Compose over XML layouts?

A) Faster app runtime performance
B) Smaller APK size
C) Declarative UI with less code and better type safety
D) Works on older Android versions

### Question 3
What file declares app components, permissions, and metadata?

A) build.gradle.kts
B) MainActivity.kt
C) AndroidManifest.xml
D) settings.gradle.kts

### Question 4
What does the `@Composable` annotation do?

A) Makes a function run faster
B) Marks a function that emits UI elements
C) Automatically generates preview
D) Enables dependency injection

### Question 5
What is the purpose of the Gradle build system?

A) Run the app on devices
B) Design the user interface
C) Compile code, manage dependencies, and package the app
D) Debug runtime errors

---

## Quiz Answers

**Question 1: B) API 24 (Android 7.0)**

API 24 (Nougat) covers approximately 95-98% of active Android devices. While API 21 covers more, API 24 provides better security and modern features without sacrificing reach.

**Why not lower?**
- API 21 has security vulnerabilities
- API 24+ has better TLS support
- Modern libraries often require API 24+

---

**Question 2: C) Declarative UI with less code and better type safety**

Jetpack Compose benefits:
- **40% less code** than XML
- **Type-safe**: Compiler catches errors
- **Reactive**: UI updates automatically
- **Kotlin-first**: Lambdas, extension functions

XML required:
- Separate layout files
- `findViewById()` boilerplate
- Manual view updates
- No type safety for IDs

---

**Question 3: C) AndroidManifest.xml**

The manifest file declares:
```xml
<manifest>
    <uses-permission android:name="android.permission.INTERNET" />

    <application android:label="App Name" android:icon="@mipmap/ic_launcher">
        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

**Other files**:
- `build.gradle.kts`: Dependencies, SDK versions
- `MainActivity.kt`: Activity code
- `settings.gradle.kts`: Project modules

---

**Question 4: B) Marks a function that emits UI elements**

`@Composable` tells the compiler:
- This function describes UI
- Can call other `@Composable` functions
- Can only be called from composable context

```kotlin
@Composable
fun MyScreen() {
    Text("Hello")  // OK: Text is @Composable
}

fun regularFunction() {
    Text("Hello")  // ERROR: Can't call @Composable from non-composable
}
```

---

**Question 5: C) Compile code, manage dependencies, and package the app**

Gradle handles the entire build process:

```
Source Code + Resources + Dependencies
         ↓
    Gradle Build
         ↓
  Compilation → DEX → APK/AAB
```

Tasks:
- Download libraries from Maven
- Compile Kotlin to bytecode
- Convert to DEX format
- Package into APK/AAB
- Sign with keystore
- Run tests

---

## What You've Learned

✅ Android platform architecture and version history
✅ Benefits of Jetpack Compose over XML layouts
✅ How to install and configure Android Studio (Ladybug/Otter)
✅ Creating a new Android project with Compose
✅ Understanding Android project structure (manifest, build files, resources)
✅ How Gradle builds and packages Android apps
✅ Running apps on emulator and physical devices
✅ Using debugging tools (Logcat, adb, Layout Inspector)
✅ Version catalog (libs.versions.toml) for dependency management

---

## Next Steps

In **Lesson 6.2: Introduction to Jetpack Compose**, you'll dive deep into Compose:
- Composable functions and the declarative paradigm
- Preview annotations for rapid development
- Basic UI components (Text, Button, Image)
- Modifiers for styling and layout
- State management with `remember` and `mutableStateOf`
- Building interactive UIs

Get ready to build beautiful, reactive UIs with modern Android development!
