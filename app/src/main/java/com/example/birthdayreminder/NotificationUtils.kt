package com.example.birthdayreminder

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*


fun setNotification(timeInMilliSeconds: Long, activity: Activity, title: String, message: String) {

    if (timeInMilliSeconds > 0) {

        val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(
            activity.applicationContext,
            AlarmReceiver::class.java
        )

        alarmIntent.putExtra("title", title)
        alarmIntent.putExtra("message", message)
        alarmIntent.putExtra("reason", "notification")
        alarmIntent.putExtra("timestamp", timeInMilliSeconds)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMilliSeconds

        val pendingIntent =
            PendingIntent.getBroadcast(activity, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

    }

}


fun showNotification(context: Context, title: String, message: String) {

    val notifyIntent = Intent(context, NotificationService::class.java)

    notifyIntent.putExtra("title", title)
    notifyIntent.putExtra("message", message)
    notifyIntent.putExtra("reason", "notification")
    notifyIntent.putExtra("timestamp", Calendar.getInstance().timeInMillis)

    context.startService(notifyIntent)

}