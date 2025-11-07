package com.labstyle.coralogixlogger.models

data class CoralogixConfig(
    val privateKey: String,
    val applicationName: String,
    val subsystemName: String,
    val region: CoralogixRegion = CoralogixRegion.US1,
    val persistence: Boolean = false
)
