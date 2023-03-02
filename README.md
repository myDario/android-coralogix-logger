# android-coralogix-logger
Coralogix logger library for android

## Offline support
This library will serialize locally the unsent logs, and will send them once back online.
Logs older than 24 hours will be dropped and not sent.

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