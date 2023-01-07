package de.mgprogramms.birthdayremindr.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat


class RemoveNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        intent.extras?.getInt(NOTIFICATION_ID)
            ?.let { notificationId ->
                with(NotificationManagerCompat.from(context)) {
                    cancel(notificationId)
                }
            }
    }

    companion object {
        const val NOTIFICATION_ID = "notificationId"
    }
}
