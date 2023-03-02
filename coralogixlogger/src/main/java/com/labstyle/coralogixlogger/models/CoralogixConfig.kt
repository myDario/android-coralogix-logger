package com.labstyle.coralogixlogger.models

data class CoralogixConfig(
    val privateKey: String,
    val applicationName: String,
    val subsystemName: String
)
