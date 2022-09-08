package de.mgprogramms.birthdayreminder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import de.mgprogramms.birthdayreminder.notifications.BirthdayNotificationChannelId

class BirthdayReminderApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val birthdayReminderChannel = NotificationChannel(
            BirthdayNotificationChannelId,
            applicationContext.getString(R.string.birthday_reminder_notification_name),
            NotificationManager.IMPORTANCE_HIGH,
        )

        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(birthdayReminderChannel)
    }

}
