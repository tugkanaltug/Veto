package veto.call.blocker.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import veto.call.blocker.data.entity.BlockedContact

@Dao
interface BlockedContactDao {
    @Query("SELECT * FROM blocked_contacts ORDER BY name ASC")
    fun getAllBlockedContacts(): Flow<List<BlockedContact>>

    @Query("SELECT * FROM blocked_contacts WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getBlockedContactByNumber(phoneNumber: String): BlockedContact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blockedContact: BlockedContact)

    @Update
    suspend fun update(blockedContact: BlockedContact)

    @Delete
    suspend fun delete(blockedContact: BlockedContact)

    @Query("DELETE FROM blocked_contacts WHERE id = :id")
    suspend fun deleteById(id: Long)
}
