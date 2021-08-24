package com.mgprogramms.birthdayreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

const val NOTIFICATION_ID = "notificationId"

class RemoveNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.extras?.get(NOTIFICATION_ID) as Int

        with(NotificationManagerCompat.from(context)) {
            cancel(notificationId)
        }
    }
}