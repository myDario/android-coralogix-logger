package com.labstyle.coralogixlogger

import android.content.Context
import android.util.Log
import com.labstyle.coralogixlogger.db.CoralogixLogEntryDao
import com.labstyle.coralogixlogger.db.LogEntriesDb
import com.labstyle.coralogixlogger.models.CoralogixConfig
import com.labstyle.coralogixlogger.models.CoralogixLogEntry
import com.labstyle.coralogixlogger.models.CoralogixRegion
import com.labstyle.coralogixlogger.models.CoralogixSeverity
import com.labstyle.coralogixlogger.models.LogApiRequest
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
        subsystemName: String,
        region: CoralogixRegion = CoralogixRegion.US1,
        persistence: Boolean = true
    ) {
        config = CoralogixConfig(privateKey, applicationName, subsystemName, region, persistence)
        dbDao = LogEntriesDb.db(context)?.coralogixLogEntryDao()
        apiService = CoralogixApiService.buildService(region, privateKey, debug)
        config?.let { cfg -> dbDao?.let { dao -> apiService?.let { srv ->
            queueWorker = QueueWorker(cfg, dao, srv)
        } } }
    }

    fun log(severity: CoralogixSeverity, text: String?) {
        if (text == null) {
            return
        }

        if (queueWorker == null) {
            Log.w(internalTag, "CoralogixLogger.initialiseApp must be called before any log operation.")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                queueWorker?.addToQueue(severity, text)
                queueWorker?.processQueue()
            } catch (e: Exception) {
                if (debug) {
                    e.message?.let { Log.w(internalTag, it) }
                }
            }
        }
    }

    fun logImmediate(entries: List<CoralogixLogEntry>, onComplete: (Boolean) -> Unit = {}) {
        val entriesToSend = entries.filter { it.timestamp > QueueWorker.get24hTimestamp() }

        if (entriesToSend.isEmpty()) {
            onComplete(true)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            fun completeOnMain(arg: Boolean) {
                CoroutineScope(Dispatchers.Main).launch { onComplete(arg) }
            }
            
            try {
                config?.let { cfg ->
                    val request = LogApiRequest(
                        applicationName = cfg.applicationName,
                        subsystemName = cfg.subsystemName,
                        logEntries = entriesToSend
                    )
                    apiService?.log(request)
                    completeOnMain(true)
                    return@launch
                }
            } catch (e: Exception) {
                if (debug) {
                    e.message?.let { Log.w(internalTag, it) }
                }
            }
            completeOnMain(false)
        }
    }

    fun setDebug(debug: Boolean) {
        this.debug = debug
        config?.let { cfg ->
            apiService = CoralogixApiService.buildService(cfg.region, cfg.privateKey, CoralogixLogger.debug)
            queueWorker?.apiService = apiService as CoralogixApiService
        }
    }
}