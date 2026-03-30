package veto.call.blocker.ui

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import veto.call.blocker.data.entity.BlockedContact
import veto.call.blocker.data.entity.CallLog
import veto.call.blocker.domain.repository.VetoRepository
import veto.call.blocker.ui.blocklist.Contact
import javax.inject.Inject

@HiltViewModel
class VetoViewModel @Inject constructor(
    application: Application,
    private val repository: VetoRepository
) : AndroidViewModel(application) {

    val allBlockedContacts: StateFlow<List<BlockedContact>> = repository.allBlockedContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCallLogs: StateFlow<List<CallLog>> = repository.allCallLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    private val _deviceContacts = MutableStateFlow<List<Contact>>(emptyList())
    val deviceContacts: StateFlow<List<Contact>> = _deviceContacts.asStateFlow()

    init {
        loadDeviceContacts()
    }

    fun loadDeviceContacts() {
        viewModelScope.launch(Dispatchers.IO) {
            if (ContextCompat.checkSelfPermission(
                    getApplication(),
                    Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@launch
            }

            val contacts = mutableListOf<Contact>()
            val contentResolver = getApplication<Application>().contentResolver
            
            try {
                val cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    ),
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                )

                cursor?.use {
                    val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    while (it.moveToNext()) {
                        val name = it.getString(nameIndex)
                        val number = it.getString(numberIndex).replace(" ", "").replace("-", "")
                        contacts.add(Contact(name, number))
                    }
                }
                _deviceContacts.value = contacts.distinctBy { it.phoneNumber }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    fun addBlockedContact(contact: Contact, startTime: String, endTime: String, activeDays: String) {
        viewModelScope.launch {
            repository.insertBlockedContact(
                BlockedContact(
                    phoneNumber = contact.phoneNumber,
                    name = contact.name,
                    startTime = startTime,
                    endTime = endTime,
                    activeDays = activeDays
                )
            )
        }
    }

    fun updateBlockedContact(contact: BlockedContact) {
        viewModelScope.launch {
            repository.updateBlockedContact(contact)
        }
    }

    fun removeBlockedContact(contact: BlockedContact) {
        viewModelScope.launch {
            repository.deleteBlockedContact(contact)
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearCallLogs()
        }
    }
}
