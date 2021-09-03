package com.mgprogramms.birthdayreminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class PersistentNotificationService : Service() {

    companion object {
        const val MESSAGES = "messages"
        const val IDS = "ids"

        fun setNotifications(context: Context, notificationTexts: Array<String>, notificationIds: IntArray) {
            val intent = Intent(context, PersistentNotificationService::class.java).apply {
                putExtra(MESSAGES, notificationTexts)
                putExtra(IDS, notificationIds)
            }
            context.startService(intent)
        }
    }

    private val binder = LocalBinder()

    /**
     * Class used for the client Binder. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): PersistentNotificationService = this@PersistentNotificationService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val bundle = intent.extras
        if (bundle != null) {
            val messages = bundle.getStringArray(MESSAGES)
            val ids = bundle.getIntArray(IDS)
            if (messages != null && ids != null && messages.isNotEmpty() && ids.isNotEmpty() && messages.size == ids.size) {
                var hasDisplayedMessages = false
                for (i in messages.indices) {
                    var notificationId = ids[i]
                    if (!RemoveNotificationReceiver.isActivated(applicationContext) || !RemoveNotificationReceiver.hasBeenRemovedToday(applicationContext, notificationId)) {
                        showNotification(messages[i], notificationId)
                        hasDisplayedMessages = true
                    }
                }
                if (hasDisplayedMessages) {
                    return START_REDELIVER_INTENT
                }
            }
        }

        stopSelf()
        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        showNotification("Running")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotification(text: String, notificationId: Int = -1) {
        createNotificationChannel();
        var builder = NotificationCompat.Builder(applicationContext, "OngoingNotificationTest")
            .setSmallIcon(R.id.icon)
            .setContentTitle(text)
            .setContentText("BirthdayReminder")
            .setOngoing(true)
            .addAction(
                R.id.icon,
                "Done",
                PendingIntent.getBroadcast(
                    applicationContext,
                    notificationId,
                    Intent(applicationContext, RemoveNotificationReceiver::class.java).apply {
                        putExtra(RemoveNotificationReceiver.NOTIFICATION_ID, notificationId)
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setChannelId("OngoingNotificationTest")

        val whatsAppNumber = getPhoneNumberByContactId(applicationContext, notificationId)
        if (whatsAppNumber != null) {
            builder = builder.addAction(
                R.id.icon, "Send message to $whatsAppNumber",
                PendingIntent.getBroadcast(
                    applicationContext,
                    notificationId,
                    Intent(applicationContext, OpenChatReceiver::class.java).apply {
                        putExtra(CONTACT_ID, notificationId)
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        }

        val notification = builder.build();
        with(NotificationManagerCompat.from(applicationContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, notification)
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Ongoing Notifications"
            val descriptionText = "Test for an ongoing notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("OngoingNotificationTest", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}
