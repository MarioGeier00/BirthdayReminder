package com.mgprogramms.birthdayreminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class TestWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "TestChannel"
        const val CHANNEL_NAME = "TestChannel name"
        const val CHANNEL_DESCRIPTION = "TestChannel desc"

        const val NOTIFICATION_ID = 20
    }

    override fun doWork(): Result {
        NotificationLogger.addNotification(applicationContext, "TestWorker")
        showNotification()

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    private fun showNotification() {
        createNotificationChannel()

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("TestWorker")
            .setContentText("Notification text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(false)

        with(NotificationManagerCompat.from(applicationContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
