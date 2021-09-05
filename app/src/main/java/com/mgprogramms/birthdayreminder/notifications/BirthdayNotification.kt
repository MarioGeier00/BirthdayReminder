package com.mgprogramms.birthdayreminder.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mgprogramms.birthdayreminder.CONTACT_ID
import com.mgprogramms.birthdayreminder.OpenChatReceiver
import com.mgprogramms.birthdayreminder.R
import com.mgprogramms.birthdayreminder.RemoveNotificationReceiver
import com.mgprogramms.birthdayreminder.birthday.BirthdayData
import com.mgprogramms.birthdayreminder.birthday.Contacts

class BirthdayNotification {
    companion object {
        const val CHANNEL_ID = "BirthdayNotification"

        fun show(context: Context, birthdayData: BirthdayData) {
            createNotificationChannel()

            val title = "Geburtstag"
            val text = "${birthdayData.name} hat heute Geburtstag"

            var builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.id.icon)
                .setContentTitle(title)
                .setContentText(text)
                .setOngoing(true)
                .addAction(
                    R.id.icon,
                    "Done",
                    PendingIntent.getBroadcast(
                        context,
                        birthdayData.id,
                        Intent(applicationContext, RemoveNotificationReceiver::class.java).apply {
                            putExtra(RemoveNotificationReceiver.NOTIFICATION_ID, notificationId)
                        },
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val whatsAppNumber = Contacts.getPhoneNumberByContactId(applicationContext, notificationId)
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

            with(NotificationManagerCompat.from(applicationContext)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder.build())
            }
        }

        private fun createNotificationChannel() {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = applicationContext.resources.getString(R.string.notifications)
                val descriptionText =
                    applicationContext.resources.getString(R.string.notifications_description)
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
}
