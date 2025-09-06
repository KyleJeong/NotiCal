package com.young2000.notical

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.young2000.notical.service.CalendarNotificationService // Added import

class CalendarEventReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // Restart the notification service after device reboot
                val serviceIntent = Intent(context, CalendarNotificationService::class.java)
                context.startForegroundService(serviceIntent)
            }
        }
    }
}
