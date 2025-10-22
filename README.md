# android-coralogix-logger
Coralogix logger library for android

## ⚠️ Important Update - Regional Endpoints
As of December 12, 2025, Coralogix has deprecated legacy log ingestion endpoints. This library now uses the new regional endpoint format: `https://ingress.{region}.coralogix.com/logs/v1/singles`

**You must specify your Coralogix region** when initializing the logger.

## Offline support
Can be used with a persistent queue (for offline support), or with an in-memory queue (no persistence). 
Logs older than 24 hours will be dropped and not sent.

## Install
Make sure you have Jitpack repo declared in your root `build.gradle` file:
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency to your module or app:
```gradle
dependencies {
    implementation 'com.github.myDario:android-coralogix-logger:1.2.0'
}
```

## Usage
Initialize logger with your region:
```kotlin
CoralogixLogger.initializeApp(
    context = applicationContext,
    privateKey = "****",
    applicationName = "My app name",
    subsystemName = "PRD",
    region = CoralogixRegion.US1,  // Specify your Coralogix region
    persistence = true)
```

## Regional Endpoints
Available regions: `US1`, `US2`, `EU1`, `EU2`, `AP1`, `AP2`, `AP3`

URL pattern: `https://ingress.{region}.coralogix.com/logs/v1/singles`

Add log:
```kotlin
CoralogixLogger.log(CoralogixSeverity.INFO, "log info")
```

## Log Severities
```
    DEBUG
    VERBOSE
    INFO
    WARN
    ERROR
    CRITICAL
```

## View debug logs
```kotlin
CoralogixLogger.setDebug(true)
```

## Log immediately (no queue)
To send immediately a collection of log entries, use the following syntax:
```kotlin
CoralogixLogger.logImmediate(listOf(
    CoralogixLogEntry(
        id = 0,
        timestamp = System.currentTimeMillis(),
        severity = CoralogixSeverity.DEBUG.code(),
        text = "immediate")
)) { sent ->
    Log.d("CLX", "log sent: $sent")
}
```

## Migration from Legacy Endpoints
Simply add the `region` parameter matching your Coralogix account:

**Before:**
```kotlin
CoralogixLogger.initializeApp(
    context = applicationContext,
    privateKey = "****",
    applicationName = "My app",
    subsystemName = "PRD",
    persistence = true)
```

**After:**
```kotlin
CoralogixLogger.initializeApp(
    context = applicationContext,
    privateKey = "****",
    applicationName = "My app",
    subsystemName = "PRD",
    region = CoralogixRegion.US1,  // Add your region
    persistence = true)
```

## Version History

### Version 1.2.0 (Current)
- ✅ Added regional endpoint support (US1, US2, EU1, EU2, AP1, AP2, AP3)
- ✅ Migrated from deprecated legacy endpoints
- ✅ 100% backward compatible (defaults to US1)
- ✅ New URL pattern: `https://ingress.{region}.coralogix.com/logs/v1/singles`

### Version 1.1.3 (Legacy)
- Uses deprecated endpoint: `https://api.coralogix.us/api/v1/`