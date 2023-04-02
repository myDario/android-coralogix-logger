# android-coralogix-logger
Coralogix logger library for android

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
    implementation 'com.github.myDario:android-coralogix-logger:1.1.2'
}
```

## Usage
Initialize logger:
```kotlin
CoralogixLogger.initializeApp(
    context = applicationContext,
    privateKey = "****",
    applicationName = "My app name",
    subsystemName = "PRD",
    persistence = true)
```

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