package com.mgprogramms.birthdayreminder.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mgprogramms.birthdayreminder.CONTACT_ID
import com.mgprogramms.birthdayreminder.OpenChatReceiver
import com.mgprogramms.birthdayreminder.birthday.Contacts
import de.mgprogramms.birthdayreminder.R
import de.mgprogramms.birthdayreminder.models.BirthdayContact

class BirthdayNotification {
    companion object {
        private const val CHANNEL_ID = "BirthdayNotification"

        fun create(context: Context, birthdayData: BirthdayContact): Notification {
            createNotificationChannel(context)
            val title = "Geburtstag"
            val text = "${birthdayData.name} hat heute Geburtstag"

            var builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    "Done",
                    PendingIntent.getBroadcast(
                        context,
                        birthdayData.id,
                        Intent(context, RemoveNotificationReceiver::class.java).apply {
                            putExtra(RemoveNotificationReceiver.NOTIFICATION_ID, birthdayData.id)
                        },
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )

            val whatsAppNumber = Contacts.getPhoneNumberByContactId(context, birthdayData.id)
            if (whatsAppNumber != null) {
                builder = builder.addAction(
                    R.id.icon, "Send message to $whatsAppNumber",
                    PendingIntent.getBroadcast(
                        context,
                        birthdayData.id,
                        Intent(context, OpenChatReceiver::class.java).apply {
                            putExtra(CONTACT_ID, birthdayData.id)
                        },
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
            return builder.build()
        }

        fun show(context: Context, notification: Notification, id: Int) {
            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                notify(id, notification)
            }
        }


        private fun createNotificationChannel(context: Context) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = context.resources.getString(R.string.settings_birthday_notification_title)
            val descriptionText =
                context.resources.getString(R.string.settings_birthday_notification_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}
