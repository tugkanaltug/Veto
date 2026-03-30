package veto.call.blocker.domain.repository

import kotlinx.coroutines.flow.Flow
import veto.call.blocker.data.entity.BlockedContact
import veto.call.blocker.data.entity.CallLog

interface VetoRepository {
    val allBlockedContacts: Flow<List<BlockedContact>>
    val allCallLogs: Flow<List<CallLog>>

    suspend fun insertBlockedContact(contact: BlockedContact)
    suspend fun updateBlockedContact(contact: BlockedContact)
    suspend fun deleteBlockedContact(contact: BlockedContact)
    suspend fun deleteBlockedContactById(id: Long)
    suspend fun insertCallLog(log: CallLog)
    suspend fun clearCallLogs()
    suspend fun getBlockedContactByNumber(number: String): BlockedContact?
}
