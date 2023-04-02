package com.labstyle.coralogixlogger.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coralogixlogentry")
data class CoralogixLogEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "severity")
    val severity: Int,

    @ColumnInfo(name = "text")
    val text: String
)
