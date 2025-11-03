package com.labstyle.coralogixlogger.models

/**
 * Coralogix regional endpoints.
 * URL pattern: https://ingress.{region}.coralogix.com/logs/v1/singles
 */
enum class CoralogixRegion(val code: String) {
    US1("us1"),
    US2("us2"),
    EU1("eu1"),
    EU2("eu2"),
    AP1("ap1"),
    AP2("ap2"),
    AP3("ap3")
    
    fun getBaseUrl(): String = "https://ingress.$code.coralogix.com/"
}

