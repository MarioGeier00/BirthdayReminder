package com.example.birthdayreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


@Deprecated("Use BirthdayNotificationWorker instead")
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val service = Intent(context, NotificationService::class.java)
        service.putExtra("title", intent.getStringExtra("title"))
        service.putExtra("message", intent.getStringExtra("message"))
        service.putExtra("reason", intent.getStringExtra("reason"))
        service.putExtra("timestamp", intent.getLongExtra("timestamp", 0))

        context.startService(service)
    }

}