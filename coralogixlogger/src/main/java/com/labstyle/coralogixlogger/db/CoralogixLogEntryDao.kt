package com.labstyle.coralogixlogger.db

import androidx.room.*
import com.labstyle.coralogixlogger.models.CoralogixLogEntry

@Dao
interface CoralogixLogEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLogEntry(entry: CoralogixLogEntry)

    @Query("SELECT * FROM coralogixlogentry")
    suspend fun getQueue(): List<CoralogixLogEntry>

    @Delete
    suspend fun deleteLogEntry(vararg entries: CoralogixLogEntry)
}