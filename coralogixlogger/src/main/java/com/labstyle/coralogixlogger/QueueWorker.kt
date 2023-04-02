package com.labstyle.coralogixlogger

import com.labstyle.coralogixlogger.db.CoralogixLogEntryDao
import com.labstyle.coralogixlogger.models.CoralogixConfig
import com.labstyle.coralogixlogger.models.CoralogixLogEntry
import com.labstyle.coralogixlogger.models.CoralogixSeverity
import com.labstyle.coralogixlogger.models.LogApiRequest
import com.labstyle.coralogixlogger.service.CoralogixApiService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

class QueueWorker(
    private val config: CoralogixConfig,
    private val dbDao: CoralogixLogEntryDao,
    var apiService: CoralogixApiService
) {
    private var isProcessing = false
    private val inMemoryQueue = ArrayList<CoralogixLogEntry>()
    private val mutex = Mutex()

    companion object {
        fun get24hTimestamp(): Long {
            val cal = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.DATE, -1)
            // remove 5 minutes for api call time
            cal.add(Calendar.MINUTE, -5)
            return cal.timeInMillis
        }
    }

    suspend fun processQueue() = mutex.withLock {
        if (isProcessing) {
            return@withLock
        }

        isProcessing = true
        try {
            if (config.persistence) {
                dbDao.deleteOlderThan(get24hTimestamp())
            } else {
                val old = inMemoryQueue.filter { it.timestamp <= get24hTimestamp() }
                if (old.isNotEmpty()) {
                    inMemoryQueue.removeAll(old.toSet())
                }
            }

            val entries = if (config.persistence) dbDao.getQueue() else inMemoryQueue

            if (entries.isNotEmpty()) {
                val request = LogApiRequest(
                    privateKey = config.privateKey,
                    applicationName = config.applicationName,
                    subsystemName = config.subsystemName,
                    logEntries = entries
                )
                apiService.log(request)

                if (config.persistence) {
                    dbDao.deleteLogEntries(entries)
                } else {
                    inMemoryQueue.clear()
                }
            }
        } finally {
            isProcessing = false
        }
    }

    suspend fun addToQueue(severity: CoralogixSeverity, text: String) {
        val entry = CoralogixLogEntry(
            timestamp = System.currentTimeMillis(),
            severity = severity.code(),
            text = text
        )

        if (config.persistence) {
            dbDao.saveLogEntry(entry)
        } else {
            inMemoryQueue.add(entry)
        }
    }
}