package de.mgprogramms.birthdayremindr.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.SimPhonebookContract.SimRecords.PHONE_NUMBER
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.mgprogramms.birthdayremindr.R
import de.mgprogramms.birthdayremindr.models.BirthdayContact
import de.mgprogramms.birthdayremindr.providers.PhoneNumberProvider
import de.mgprogramms.birthdayremindr.providers.RawContactIdProvider
import de.mgprogramms.birthdayremindr.receivers.CONTACT_ID
import de.mgprogramms.birthdayremindr.receivers.OpenChatReceiver
import de.mgprogramms.birthdayremindr.receivers.RemoveNotificationReceiver

const val BirthdayNotificationChannelId = "birthday_reminder_notification"


class BirthdayNotification(val context: Context, val birthdayData: BirthdayContact) {
    val title = context.getString(R.string.birthday_notification_title)
    val text = context.getString(R.string.birthday_notification_message, birthdayData.name)

    val builder = NotificationCompat.Builder(context, BirthdayNotificationChannelId)
        .setSmallIcon(R.drawable.ic_cake)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setOngoing(true)
        .addAction(
            R.drawable.ic_check,
            context.getString(R.string.notification_done),
            PendingIntent.getBroadcast(
                context,
                -birthdayData.id,
                Intent(context, RemoveNotificationReceiver::class.java).apply {
                    putExtra(RemoveNotificationReceiver.NOTIFICATION_ID, birthdayData.id)
                },
                PendingIntent.FLAG_MUTABLE
            )
        )


    fun create(): Notification {
        return RawContactIdProvider(context)
            .getWhatsAppRawContactIds(birthdayData.id)
            .firstOrNull()
            ?.let { PhoneNumberProvider(context).getPhoneNumbersByRawContactId(birthdayData.id, it) }
            ?.firstOrNull()
            ?.let { whatsAppNumber ->
                builder.addAction(
                    R.drawable.ic_send,
                    context.getString(R.string.notification_sent_message, whatsAppNumber),
                    PendingIntent.getBroadcast(
                        context,
                        birthdayData.id,
                        Intent(context, OpenChatReceiver::class.java).apply {
                            putExtra(CONTACT_ID, birthdayData.id)
                            putExtra(PHONE_NUMBER, whatsAppNumber)
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
