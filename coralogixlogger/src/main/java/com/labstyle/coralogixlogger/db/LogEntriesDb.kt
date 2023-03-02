package com.labstyle.coralogixlogger.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.labstyle.coralogixlogger.models.CoralogixLogEntry

@Database(entities = [CoralogixLogEntry::class], version = 1, exportSchema = false)
abstract class LogEntriesDb: RoomDatabase() {
    abstract fun coralogixLogEntryDao(): CoralogixLogEntryDao

    companion object {
        private var dbInstance: LogEntriesDb? = null

        fun db(context: Context): LogEntriesDb? {
            if (dbInstance == null) {
                val builder = Room.databaseBuilder(context, LogEntriesDb::class.java, "CoralogixLogs")
                dbInstance = builder.build()
            }
            return dbInstance
        }
    }
}