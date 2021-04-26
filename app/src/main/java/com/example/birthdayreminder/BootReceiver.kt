package com.example.birthdayreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi


class BootReceiver : BroadcastReceiver() {

    val bootCompletedAction = "android.intent.action.BOOT_COMPLETED";

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {

        // if the current call to this receiver is a bootup call
        // then start the necessary workers
        if (intent?.action == bootCompletedAction) {
            BirthdayNotificationWorker.enqueueSelf(context)
        }

//      TODO: Remove code
//      var alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//      var alarmIntent = Intent(context, AlarmReceiver::class.java)
//      alarmIntent.putExtra("title", "title")
//      alarmIntent.putExtra("message", "message")
//      alarmIntent.putExtra("reason", "notification")
//      alarmIntent.putExtra("timestamp", Calendar.getInstance().timeInMillis)
//
//      var pendingAlarm = alarmIntent.let { intent ->
//          PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE)
//      }
//
//      alarmMgr?.setInexactRepeating(
//          AlarmManager.ELAPSED_REALTIME_WAKEUP,
//          SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR * 2,
//          AlarmManager.INTERVAL_HOUR * 2,
//          pendingAlarm
//      )

    }

}