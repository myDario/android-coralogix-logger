package com.labstyle.coralogixlogger.models

data class LogApiRequest(
    val privateKey: String,
    val applicationName: String,
    val subsystemName: String,
    val logEntries: List<CoralogixLogEntry>
)