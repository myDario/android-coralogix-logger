package com.labstyle.coralogixlogger.models

data class LogApiRequest(
    val applicationName: String,
    val subsystemName: String,
    val logEntries: List<CoralogixLogEntry>
)