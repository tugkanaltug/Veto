package veto.call.blocker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_contacts")
data class BlockedContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val name: String?,
    val startTime: String?, // Format: HH:mm
    val endTime: String?,   // Format: HH:mm
    val activeDays: String?  // Comma-separated days of week, e.g., "1,2,3,4,5" (1=Sun, 7=Sat)
)
