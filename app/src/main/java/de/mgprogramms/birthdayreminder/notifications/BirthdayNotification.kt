package de.mgprogramms.birthdayreminder.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mgprogramms.birthdayreminder.CONTACT_ID
import com.mgprogramms.birthdayreminder.OpenChatReceiver
import com.mgprogramms.birthdayreminder.birthday.Contacts
import com.mgprogramms.birthdayreminder.notifications.RemoveNotificationReceiver
import de.mgprogramms.birthdayreminder.R
import de.mgprogramms.birthdayreminder.models.BirthdayContact

const val BirthdayNotificationChannelId = "birthday_reminder_notification"


class BirthdayNotification(val context: Context, val birthdayData: BirthdayContact) {
    val title = "Geburtstag"
    val text = "${birthdayData.name} hat heute Geburtstag"

    val builder = NotificationCompat.Builder(context, BirthdayNotificationChannelId)
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


    fun create(): Notification {
        return Contacts.getPhoneNumberByContactId(context, birthdayData.id)?.let {
            builder.addAction(
                R.id.icon, "Send message to $it",
                PendingIntent.getBroadcast(
                    context,
                    birthdayData.id,
                    Intent(context, OpenChatReceiver::class.java).apply {
                        putExtra(CONTACT_ID, birthdayData.id)
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            ).build()
        } ?: builder.build()
    }

    fun show(context: Context, notification: Notification, id: Int) {
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(id, notification)
        }
    }
}
