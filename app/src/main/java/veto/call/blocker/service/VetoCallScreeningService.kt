package veto.call.blocker.service

import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import veto.call.blocker.data.entity.CallLog
import veto.call.blocker.domain.repository.VetoRepository
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class VetoCallScreeningService : CallScreeningService() {
    
    @Inject
    lateinit var repository: VetoRepository
    
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumberUri = callDetails.handle
        if (phoneNumberUri == null) {
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }

        val rawNumber = phoneNumberUri.schemeSpecificPart
        val formattedNumber = formatPhoneNumber(rawNumber)

        serviceScope.launch {
            val blockedContact = repository.getBlockedContactByNumber(formattedNumber)

            if (blockedContact != null && isCurrentlyInSchedule(blockedContact.startTime, blockedContact.endTime, blockedContact.activeDays)) {
                // Reject the call
                val response = CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipCallLog(false)
                    .setSkipNotification(false)
                    .build()
                
                respondToCall(callDetails, response)

                // Log the blocked call
                repository.insertCallLog(
                    CallLog(
                        phoneNumber = rawNumber,
                        callerName = blockedContact.name,
                        timestamp = System.currentTimeMillis(),
                        wasBlocked = true
                    )
                )
            } else {
                respondToCall(callDetails, CallResponse.Builder().build())
            }
        }
    }

    private fun formatPhoneNumber(number: String): String {
        return number.replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
    }

    private fun isCurrentlyInSchedule(startTime: String?, endTime: String?, activeDays: String?): Boolean {
        if (startTime == null || endTime == null) return true // Block always if no schedule

        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK) // 1=Sun, 7=Sat
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentTimeInMinutes = currentHour * 60 + currentMinute

        // Check day
        if (!activeDays.isNullOrBlank()) {
            val days = activeDays.split(",").mapNotNull { it.trim().toIntOrNull() }
            if (!days.contains(currentDay)) return false
        }

        // Check time range
        val startParts = startTime.split(":").mapNotNull { it.toIntOrNull() }
        val endParts = endTime.split(":").mapNotNull { it.toIntOrNull() }
        
        if (startParts.size != 2 || endParts.size != 2) return true

        val startMinutes = startParts[0] * 60 + startParts[1]
        val endMinutes = endParts[0] * 60 + endParts[1]

        return if (startMinutes <= endMinutes) {
            currentTimeInMinutes in startMinutes..endMinutes
        } else {
            // Overnights (e.g., 22:00 to 06:00)
            currentTimeInMinutes >= startMinutes || currentTimeInMinutes <= endMinutes
        }
    }
}
