# Integration Guide - Using This Library in Your Android Project

## Quick Start

### Step 1: Add JitPack Repository

In your **project-level** `build.gradle` or `settings.gradle`:

**For build.gradle:**
```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**For settings.gradle (newer projects):**
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2: Add Dependency

In your **app-level** `build.gradle`:
```gradle
dependencies {
    implementation 'com.github.myDario:android-coralogix-logger:1.2.0'
}
```

### Step 3: Sync Project
Click "Sync Now" in Android Studio.

---

## Implementation

### Option 1: Initialize in Application Class (Recommended)

Create or update your Application class:

```kotlin
import android.app.Application
import com.labstyle.coralogixlogger.CoralogixLogger
import com.labstyle.coralogixlogger.models.CoralogixRegion

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Coralogix Logger
        CoralogixLogger.initializeApp(
            context = applicationContext,
            privateKey = "your-coralogix-private-key",
            applicationName = "My Android App",
            subsystemName = "Production",
            region = CoralogixRegion.US1,  // Change to your region
            persistence = true  // Enable offline support
        )
        
        // Enable debug logs in debug builds
        if (BuildConfig.DEBUG) {
            CoralogixLogger.setDebug(true)
        }
    }
}
```

**Don't forget to register in AndroidManifest.xml:**
```xml
<application
    android:name=".MyApplication"
    ...>
</application>
```

### Option 2: Initialize in MainActivity

If you don't want to create an Application class:

```kotlin
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.labstyle.coralogixlogger.CoralogixLogger
import com.labstyle.coralogixlogger.models.CoralogixRegion

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize once
        CoralogixLogger.initializeApp(
            context = applicationContext,
            privateKey = "your-coralogix-private-key",
            applicationName = "My Android App",
            subsystemName = "Production",
            region = CoralogixRegion.US1,
            persistence = true
        )
    }
}
```

---

## Usage Examples

### Basic Logging

```kotlin
import com.labstyle.coralogixlogger.CoralogixLogger
import com.labstyle.coralogixlogger.models.CoralogixSeverity

class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Log different severity levels
        CoralogixLogger.log(CoralogixSeverity.DEBUG, "Debug message")
        CoralogixLogger.log(CoralogixSeverity.INFO, "Info message")
        CoralogixLogger.log(CoralogixSeverity.WARN, "Warning message")
        CoralogixLogger.log(CoralogixSeverity.ERROR, "Error message")
        CoralogixLogger.log(CoralogixSeverity.CRITICAL, "Critical error!")
    }
}
```

### Logging User Actions

```kotlin
button.setOnClickListener {
    CoralogixLogger.log(CoralogixSeverity.INFO, "Button clicked: ${button.text}")
    // Your button logic
}
```

### Logging API Responses

```kotlin
try {
    val response = api.fetchData()
    CoralogixLogger.log(CoralogixSeverity.INFO, "API success: ${response.code()}")
} catch (e: Exception) {
    CoralogixLogger.log(CoralogixSeverity.ERROR, "API error: ${e.message}")
}
```

### Logging with Context

```kotlin
fun loginUser(username: String) {
    CoralogixLogger.log(CoralogixSeverity.INFO, "Login attempt for user: $username")
    
    try {
        // Login logic
        CoralogixLogger.log(CoralogixSeverity.INFO, "Login successful for: $username")
    } catch (e: Exception) {
        CoralogixLogger.log(CoralogixSeverity.ERROR, "Login failed for $username: ${e.message}")
    }
}
```

### Immediate Logging (Bypass Queue)

```kotlin
import com.labstyle.coralogixlogger.models.CoralogixLogEntry

val urgentLog = CoralogixLogEntry(
    id = 0,
    timestamp = System.currentTimeMillis(),
    severity = CoralogixSeverity.CRITICAL.code(),
    text = "Critical system error!"
)

CoralogixLogger.logImmediate(listOf(urgentLog)) { sent ->
    if (sent) {
        Log.d("MyApp", "Urgent log sent successfully")
    } else {
        Log.e("MyApp", "Failed to send urgent log")
    }
}
```

---

## Configuration Options

### Available Regions

```kotlin
CoralogixRegion.US1  // United States
CoralogixRegion.US2  // United States 2
CoralogixRegion.EU1  // Europe (Ireland)
CoralogixRegion.EU2  // Europe (Stockholm)
CoralogixRegion.AP1  // Asia Pacific (India)
CoralogixRegion.AP2  // Asia Pacific (Singapore)
CoralogixRegion.AP3  // Asia Pacific (Mumbai)
```

### Persistence Options

```kotlin
// With persistence (offline support) - RECOMMENDED
CoralogixLogger.initializeApp(
    context = applicationContext,
    privateKey = "your-key",
    applicationName = "My App",
    subsystemName = "Production",
    region = CoralogixRegion.US1,
    persistence = true  // Logs saved to local database
)

// Without persistence (memory only)
CoralogixLogger.initializeApp(
    context = applicationContext,
    privateKey = "your-key",
    applicationName = "My App",
    subsystemName = "Production",
    region = CoralogixRegion.US1,
    persistence = false  // Logs stored in memory only
)
```

---

## Best Practices

### 1. Store Private Key Securely

**DON'T:** Hardcode in your app
```kotlin
privateKey = "abc123xyz"  // ❌ BAD
```

**DO:** Use BuildConfig or local.properties
```gradle
// In app/build.gradle
android {
    defaultConfig {
        buildConfigField("String", "CORALOGIX_KEY", "\"${project.findProperty("coralogix.key")}\"")
    }
}
```

```kotlin
// In your code
privateKey = BuildConfig.CORALOGIX_KEY  // ✅ GOOD
```

### 2. Use Appropriate Subsystem Names

```kotlin
// Production
subsystemName = "Production"

// Development
subsystemName = if (BuildConfig.DEBUG) "Development" else "Production"

// Feature-specific
subsystemName = "Payment-Service"
```

### 3. Enable Debug Mode Conditionally

```kotlin
CoralogixLogger.setDebug(BuildConfig.DEBUG)  // Only in debug builds
```

### 4. Log Meaningful Messages

```kotlin
// ❌ BAD
CoralogixLogger.log(CoralogixSeverity.INFO, "error")

// ✅ GOOD
CoralogixLogger.log(CoralogixSeverity.ERROR, "Payment API failed: timeout after 30s for order #12345")
```

---

## Troubleshooting

### Logs Not Appearing?

1. **Check your private key** - Verify it's correct in Coralogix dashboard
2. **Verify region** - Make sure you're using the correct region for your account
3. **Check network** - Logs require internet connectivity
4. **Enable debug mode** - See HTTP requests in Logcat
   ```kotlin
   CoralogixLogger.setDebug(true)
   ```
5. **Check Logcat** - Look for "CXL" tag for internal logs

### Build Errors?

1. **Sync Gradle** - File → Sync Project with Gradle Files
2. **Clean Build** - Build → Clean Project, then Build → Rebuild Project
3. **Check JitPack** - Visit https://jitpack.io/#myDario/android-coralogix-logger/1.2.0

### ProGuard/R8 Issues?

The library includes consumer ProGuard rules, but if needed, add:
```proguard
-keep class com.labstyle.coralogixlogger.** { *; }
```

---

## Complete Example Project Setup

Here's a complete minimal example:

**build.gradle (Project level):**
```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**build.gradle (App level):**
```gradle
dependencies {
    implementation 'com.github.myDario:android-coralogix-logger:1.2.0'
    // ... other dependencies
}
```

**MyApplication.kt:**
```kotlin
package com.mycompany.myapp

import android.app.Application
import com.labstyle.coralogixlogger.CoralogixLogger
import com.labstyle.coralogixlogger.models.CoralogixRegion

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        CoralogixLogger.initializeApp(
            context = applicationContext,
            privateKey = BuildConfig.CORALOGIX_KEY,
            applicationName = "MyAwesomeApp",
            subsystemName = if (BuildConfig.DEBUG) "Dev" else "Prod",
            region = CoralogixRegion.US1,
            persistence = true
        )
        
        CoralogixLogger.setDebug(BuildConfig.DEBUG)
    }
}
```

**AndroidManifest.xml:**
```xml
<manifest>
    <application
        android:name=".MyApplication"
        android:label="@string/app_name">
        <!-- Your activities -->
    </application>
</manifest>
```

**MainActivity.kt:**
```kotlin
package com.mycompany.myapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.labstyle.coralogixlogger.CoralogixLogger
import com.labstyle.coralogixlogger.models.CoralogixSeverity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        CoralogixLogger.log(CoralogixSeverity.INFO, "App started successfully")
    }
}
```

---

## Need Help?

- 📖 Read the [README.md](README.md)
- 🚀 Check [RELEASE.md](RELEASE.md) for version info
- 💬 Open an issue on GitHub
- 📧 Contact support

---

**Version:** 1.2.0  
**Last Updated:** October 2025

