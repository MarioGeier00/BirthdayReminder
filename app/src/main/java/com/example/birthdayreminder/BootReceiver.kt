package com.example.birthdayreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import java.util.*


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        var alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var alarmIntent = Intent(context, AlarmReceiver::class.java)
        alarmIntent.putExtra("title", "title")
        alarmIntent.putExtra("message", "message")
        alarmIntent.putExtra("reason", "notification")

        var pendingAlarm = alarmIntent.let { intent ->
            intent.putExtra("timestamp", Calendar.getInstance().timeInMillis)
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        alarmMgr?.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 3000,
            3000,
            pendingAlarm
        )
    }
}