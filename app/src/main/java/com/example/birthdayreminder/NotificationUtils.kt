package com.example.birthdayreminder

import android.R
import android.R.attr.delay
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*


fun setNotification(timeInMilliSeconds: Long, activity: Activity, title: String, message: String) {

    //------------  alarm settings start  -----------------//

    if (timeInMilliSeconds > 0) {


        val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(activity.applicationContext, AlarmReceiver::class.java) // AlarmReceiver1 = broadcast receiver

        alarmIntent.putExtra("title", title)
        alarmIntent.putExtra("message", message)
        alarmIntent.putExtra("reason", "notification")
        alarmIntent.putExtra("timestamp", timeInMilliSeconds)


        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMilliSeconds


        val pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

    }

    //------------ end of alarm settings  -----------------//


}


private val COMMENT_NOTIFICATION_ID = 100;
fun NotificationManager.sendNotification(
    title: String,
    message: String,
    notificationID: Int?,
    channel: String,
    applicationContext: Context
) {
    //TODO("Create Notification")
    val builder = NotificationCompat.Builder(
        applicationContext,
        channel
    )
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setSmallIcon(R.mipmap.sym_def_app_icon)
        .setContentTitle(title)
        .setContentText(message)
        .setDefaults(Notification.DEFAULT_SOUND)


    val notificationIntent = Intent(applicationContext, MyNotificationPublisher::class.java)

    if (notificationID != null) {
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notificationID)
    } else {
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, COMMENT_NOTIFICATION_ID)
    }
    notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, builder.build())
    val pendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        COMMENT_NOTIFICATION_ID,
        notificationIntent,
        PendingIntent.FLAG_CANCEL_CURRENT
    )

    val futureInMillis = System.currentTimeMillis() + 5000
    val am = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    am.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent)
}

fun NotificationManager.cancelNotification() = cancelAll()


fun createChannel(
    notificationManager: NotificationManager,
    channelId: String,
    channelName: String
) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            //TODO ("Change Importance as per requirement")
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            enableVibration(true)
            lightColor = Color.GREEN
        }

        notificationManager.createNotificationChannel(notificationChannel)
    }
}


class MyNotificationPublisher : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("§adfa", "onReceive")
        Log.d("§adfa", NOTIFICATION_ID)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification? = intent.getParcelableExtra(NOTIFICATION)
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
        notificationManager.notify(notificationId, notification)
    }

    companion object {
        var NOTIFICATION_ID = "notification_id"
        var NOTIFICATION = "notification"
    }
}