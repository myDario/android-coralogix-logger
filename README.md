# android-coralogix-logger
Coralogix logger library for android

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