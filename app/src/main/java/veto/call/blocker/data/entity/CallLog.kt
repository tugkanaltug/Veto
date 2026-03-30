package veto.call.blocker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_logs")
data class CallLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val callerName: String?,
    val timestamp: Long,
    val wasBlocked: Boolean
)
