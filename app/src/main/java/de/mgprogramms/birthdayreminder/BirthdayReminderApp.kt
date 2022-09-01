package de.mgprogramms.birthdayreminder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import de.mgprogramms.birthdayreminder.notifications.BirthdayNotificationChannelId

class BirthdayReminderApp : Application() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        context = applicationContext
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
