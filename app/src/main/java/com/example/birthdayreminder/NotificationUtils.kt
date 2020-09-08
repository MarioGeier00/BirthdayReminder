package com.example.birthdayreminder

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

private val COMMENT_NOTIFICATION_ID = 100;
fun NotificationManager.sendNotification(
    title: String,
    message: String,
    channel: String,
    applicationContext: Context
) {
    //TODO("Create Notification")
    val builder = NotificationCompat.Builder(
        applicationContext,
        channel
    )
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(message)
    notify(COMMENT_NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotification() = cancelAll()
