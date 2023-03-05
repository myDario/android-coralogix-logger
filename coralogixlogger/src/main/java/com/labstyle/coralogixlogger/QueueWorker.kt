package com.labstyle.coralogixlogger

import com.labstyle.coralogixlogger.db.CoralogixLogEntryDao
import com.labstyle.coralogixlogger.models.CoralogixConfig
import com.labstyle.coralogixlogger.models.LogApiRequest
import com.labstyle.coralogixlogger.service.CoralogixApiService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

class QueueWorker(
    private val config: CoralogixConfig,
    private val dbDao: CoralogixLogEntryDao,
    private val apiService: CoralogixApiService
) {
    private var isProcessing = false
    private val mutex = Mutex()

    suspend fun processQueue() = mutex.withLock {
        if (isProcessing) {
            return@withLock
        }

        isProcessing = true
        try {
            dbDao.deleteOlderThan(get24hTimestamp())

            val entries = dbDao.getQueue()

            if (entries.isNotEmpty()) {
                val request = LogApiRequest(
                    privateKey = config.privateKey,
                    applicationName = config.applicationName,
                    subsystemName = config.subsystemName,
                    logEntries = entries
                )
                apiService.log(request)

                dbDao.deleteLogEntries(entries)
            }
        } finally {
            isProcessing = false
        }
    }

    private fun get24hTimestamp(): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal.add(Calendar.DATE, -1)
        // remove 5 minutes for api call time
        cal.add(Calendar.MINUTE, -5)
        return cal.timeInMillis
    }
}