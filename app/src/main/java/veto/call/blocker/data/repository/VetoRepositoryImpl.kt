package veto.call.blocker.data.repository

import kotlinx.coroutines.flow.Flow
import veto.call.blocker.data.dao.BlockedContactDao
import veto.call.blocker.data.dao.CallLogDao
import veto.call.blocker.data.entity.BlockedContact
import veto.call.blocker.data.entity.CallLog
import veto.call.blocker.domain.repository.VetoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VetoRepositoryImpl @Inject constructor(
    private val blockedContactDao: BlockedContactDao,
    private val callLogDao: CallLogDao
) : VetoRepository {
    override val allBlockedContacts: Flow<List<BlockedContact>> = blockedContactDao.getAllBlockedContacts()
    override val allCallLogs: Flow<List<CallLog>> = callLogDao.getAllCallLogs()

    override suspend fun insertBlockedContact(contact: BlockedContact) {
        blockedContactDao.insert(contact)
    }

    override suspend fun updateBlockedContact(contact: BlockedContact) {
        blockedContactDao.update(contact)
    }

    override suspend fun deleteBlockedContact(contact: BlockedContact) {
        blockedContactDao.delete(contact)
    }

    override suspend fun deleteBlockedContactById(id: Long) {
        blockedContactDao.deleteById(id)
    }

    override suspend fun insertCallLog(log: CallLog) {
        callLogDao.insert(log)
    }

    override suspend fun clearCallLogs() {
        callLogDao.clearLogs()
    }
    
    override suspend fun getBlockedContactByNumber(number: String): BlockedContact? {
        return blockedContactDao.getBlockedContactByNumber(number)
    }
}
