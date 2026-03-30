package veto.call.blocker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class VetoBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Logic to ensure the service or any necessary background work is initialized
            // Note: CallScreeningService is managed by the system, but we might need to 
            // reschedule any worker or initialize state here if needed.
        }
    }
}
