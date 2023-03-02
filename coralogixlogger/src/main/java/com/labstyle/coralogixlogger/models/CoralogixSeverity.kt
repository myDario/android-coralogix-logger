package com.labstyle.coralogixlogger.models

enum class CoralogixSeverity {
    DEBUG,
    VERBOSE,
    INFO,
    WARN,
    ERROR,
    CRITICAL;

    fun code(): Int = when(this) {
        DEBUG -> 1
        VERBOSE -> 2
        INFO -> 3
        WARN -> 4
        ERROR -> 5
        CRITICAL -> 6
    }
}