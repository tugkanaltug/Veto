package veto.call.blocker.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import veto.call.blocker.data.entity.CallLog

@Dao
interface CallLogDao {
    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun getAllCallLogs(): Flow<List<CallLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(callLog: CallLog)

    @Query("DELETE FROM call_logs")
    suspend fun clearLogs()
}
