package com.labstyle.coralogixlogger

import android.content.Context
import android.util.Log
import com.labstyle.coralogixlogger.db.CoralogixLogEntryDao
import com.labstyle.coralogixlogger.db.LogEntriesDb
import com.labstyle.coralogixlogger.models.CoralogixConfig
import com.labstyle.coralogixlogger.models.CoralogixLogEntry
import com.labstyle.coralogixlogger.models.CoralogixSeverity
import com.labstyle.coralogixlogger.service.CoralogixApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val internalTag = "CXL"

object CoralogixLogger {
    private var config: CoralogixConfig? = null
    private var dbDao: CoralogixLogEntryDao? = null
    private var apiService: CoralogixApiService? = null
    private var queueWorker: QueueWorker? = null
    private var debug = false

    fun initializeApp(
        context: Context,
        privateKey: String,
        applicationName: String,
        subsystemName: String
    ) {
        config = CoralogixConfig(privateKey, applicationName, subsystemName)
        dbDao = LogEntriesDb.db(context)?.coralogixLogEntryDao()
        apiService = CoralogixApiService.buildService(debug)
        config?.let { cfg -> dbDao?.let { dao -> apiService?.let { srv ->
            queueWorker = QueueWorker(cfg, dao, srv)
        } } }
    }

    fun log(severity: CoralogixSeverity, text: String) {
        if (queueWorker == null) {
            Log.w(internalTag, "CoralogixLogger.initialiseApp must be called before any log operation.")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                addToQueue(severity, text)
                queueWorker?.processQueue()
            } catch (e: Exception) {
                if (debug) {
                    e.message?.let { Log.w(internalTag, it) }
                }
            }
        }
    }

    private suspend fun addToQueue(severity: CoralogixSeverity, text: String) {
        val entry = CoralogixLogEntry(
            timestamp = System.currentTimeMillis(),
            severity = severity.code(),
            text = text
        )
        dbDao?.saveLogEntry(entry)
    }

    fun setDebug(debug: Boolean) {
        this.debug = debug
        apiService = CoralogixApiService.buildService(CoralogixLogger.debug)
    }
}