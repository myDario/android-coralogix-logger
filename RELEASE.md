# Release Guide

## Publishing a New Version to JitPack

### 1. Update Version Number
Version has been updated to **1.2.0** in:
- ✅ `coralogixlogger/build.gradle` (versionCode: 120, versionName: "1.2.0")
- ✅ `README.md` (implementation version: 1.2.0)

### 2. Commit and Push Changes
```bash
git add .
git commit -m "Release v1.2.0 - Add regional endpoint support"
git push origin main
```

### 3. Create a Git Tag
```bash
git tag -a 1.2.0 -m "Version 1.2.0 - Regional Endpoints Support"
git push origin 1.2.0
```

### 4. JitPack Build
JitPack will automatically build the library when you:
- Create a new release tag
- Push to the repository

Visit: `https://jitpack.io/#myDario/android-coralogix-logger`

### 5. Using the Library in Your Android Project

**Step 1: Add JitPack repository to your root `build.gradle`:**
```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2: Add the dependency in your app's `build.gradle`:**
```gradle
dependencies {
    implementation 'com.github.myDario:android-coralogix-logger:1.2.0'
}
```

**Step 3: Sync Gradle and you're ready to use!**

### 6. Initialize in Your App

```kotlin
import com.labstyle.coralogixlogger.CoralogixLogger
import com.labstyle.coralogixlogger.models.CoralogixRegion
import com.labstyle.coralogixlogger.models.CoralogixSeverity

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        CoralogixLogger.initializeApp(
            context = applicationContext,
            privateKey = "your-private-key",
            applicationName = "My App",
            subsystemName = "Production",
            region = CoralogixRegion.US1,  // Choose your region
            persistence = true
        )
        
        // Optional: Enable debug logs
        CoralogixLogger.setDebug(BuildConfig.DEBUG)
    }
}
```

### 7. Use in Your Code

```kotlin
// Log messages
CoralogixLogger.log(CoralogixSeverity.INFO, "User logged in")
CoralogixLogger.log(CoralogixSeverity.ERROR, "API call failed")
CoralogixLogger.log(CoralogixSeverity.DEBUG, "Debug information")
```

## Version 1.2.0 Release Notes

### What's New
- **Regional Endpoint Support**: Added support for 7 global regions (US1, US2, EU1, EU2, AP1, AP2, AP3)
- **Modern Infrastructure**: Migrated from deprecated legacy endpoints to new regional infrastructure
- **Improved Reliability**: Better security, scalability, and reliability
- **Backward Compatible**: Defaults to US1 region, existing code continues to work

### Breaking Changes
- None! Fully backward compatible.

### Migration Required
Users should explicitly specify their Coralogix region by adding the `region` parameter to `initializeApp()`.

### Minimum Requirements
- minSdk: 24
- targetSdk: 33
- Kotlin: 1.7.20+

## Testing Before Release

1. Build the library:
```bash
./gradlew :coralogixlogger:build
```

2. Run tests:
```bash
./gradlew :coralogixlogger:test
```

3. Test in sample app:
```bash
./gradlew :app:build
./gradlew :app:installDebug
```

## Rollback Plan

If issues are found, users can revert to the previous version:
```gradle
implementation 'com.github.myDario:android-coralogix-logger:1.1.3'
```

## Support

For issues or questions:
- Open an issue on GitHub
- Check README.md for documentation
- Contact: [Your contact information]

