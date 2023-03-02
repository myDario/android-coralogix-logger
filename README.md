# android-coralogix-logger
Coralogix logger library for android

## Offline support
This library will serialize locally the unsent logs, and will send them once back online.
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
    implementation 'com.github.myDario:android-coralogix-logger:1.0.6'
}
```

## Usage
Initialize logger:
```kotlin
CoralogixLogger.initializeApp(
    context = applicationContext,
    privateKey = "****",
    applicationName = "My app name",
    subsystemName = "PRD")
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