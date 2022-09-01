package com.mgprogramms.birthdayreminder.birthday

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.mgprogramms.birthdayreminder.CONTACT_ID
import com.mgprogramms.birthdayreminder.NotificationLogger
import com.mgprogramms.birthdayreminder.OpenChatReceiver
import com.mgprogramms.birthdayreminder.notifications.RemoveNotificationReceiver
import com.mgprogramms.birthdayreminder.parseDate
import de.mgprogramms.birthdayreminder.R
import java.time.Duration
import java.time.LocalTime
import java.util.*

class BirthdayWorker @RequiresApi(Build.VERSION_CODES.O) constructor(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(
    appContext,
    workerParams
) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        NotificationLogger.addNotification(applicationContext, "BirthdayNotificationWorker start")
        val sharedPref = applicationContext.getSharedPreferences(
            "BirthdayReminderNotifierWorker",
            Context.MODE_PRIVATE
        )

        if (!sharedPref.getBoolean("notified", false)) {
            showNotification("BirthdayReminderNotifierWorker started")
            with(sharedPref.edit()) {
                putBoolean("notified", true)
                apply()
            }
        }


        notifyAboutBirthdays(applicationContext)

        NotificationLogger.addNotification(applicationContext, "BirthdayNotificationWorker finish")
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStopped() {
        showNotification("BirthdayNotificationWorker stopped")
        NotificationLogger.addNotification(applicationContext, "BirthdayNotificationWorker stopped")
        super.onStopped()
    }

    companion object {
        const val WORKER_ID = "BirthdayNotificationWorker"


        @RequiresApi(Build.VERSION_CODES.O)
        fun getDurationUntilNextNotification(onlyDurationInFuture: Boolean = true): Duration {
            var duration = Duration.between(LocalTime.now(), LocalTime.of(1, 10))
            if (duration.isNegative && onlyDurationInFuture) {
                duration = duration.plusDays(1)
            }
            return duration
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun enqueueSelf(
            context: Context,
            notifyHasStarted: Boolean = true,
            restart: Boolean = false
        ) {
            if (!isActivated(context)) {
                return
            }

            val sharedPref = context.getSharedPreferences(
                WORKER_ID,
                Context.MODE_PRIVATE
            )
            with(sharedPref.edit()) {
                putBoolean("notified", !notifyHasStarted)
                apply()
            }

            var delayDuration = getDurationUntilNextNotification(false)
            if (delayDuration.isNegative) {
                delayDuration = delayDuration.plusDays(1)

                WorkManager.getInstance(context)
                    .enqueue(OneTimeWorkRequestBuilder<BirthdayWorker>().build())
            }

            val notificationWork =
                PeriodicWorkRequestBuilder<BirthdayWorker>(
                    Duration.ofHours(6),
                    Duration.ofHours(1)
                ).setInitialDelay(delayDuration).build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORKER_ID,
                    if (restart) ExistingPeriodicWorkPolicy.REPLACE else ExistingPeriodicWorkPolicy.KEEP,
                    notificationWork
                )
        }

        fun dequeueSelf(context: Context) {
            if (isActivated(context)) {
                return
            }

            WorkManager.getInstance(context).cancelUniqueWork(WORKER_ID)
        }

        fun isActivated(context: Context): Boolean {
            val sharedPref = context.getSharedPreferences(
                WORKER_ID,
                Context.MODE_PRIVATE
            )
            return sharedPref.getBoolean("active", false)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun updateState(context: Context, state: Boolean) {
            val sharedPref = context.getSharedPreferences(
                WORKER_ID,
                Context.MODE_PRIVATE
            )
            with(sharedPref.edit()) {
                putBoolean("active", state)
                apply()
            }

            if (state) {
                enqueueSelf(context, notifyHasStarted = false, restart = true)
            } else {
                dequeueSelf(context)
            }
        }

        fun enqueueAtAppStartup(context: Context): Boolean {
            val sharedPref = context.getSharedPreferences(
                WORKER_ID,
                Context.MODE_PRIVATE
            )
            return sharedPref.getBoolean("enqueueAtAppStartup", false)
        }

        fun setEnqueueAtAppStartup(context: Context, value: Boolean) {
            with(context.getSharedPreferences(WORKER_ID, Context.MODE_PRIVATE).edit()) {
                putBoolean("enqueueAtAppStartup", value)
                apply()
            }
        }

    }

    val CHANNEL_ID = "BirthdayReminderNotifier"

    val NOTIFICATION_ID_DEFAULT = -2

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(text: String, notificationId: Int = NOTIFICATION_ID_DEFAULT) {
        createNotificationChannel()

        var builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
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
            val name = applicationContext.resources.getString(R.string.settings_birthday_notification_title)
            val descriptionText =
                applicationContext.resources.getString(R.string.settings_birthday_notification_description)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun notifyAboutBirthdays(context: Context) {
        val contacts = Contacts.getContacts(context)
        if (contacts != null) {
            val calendar = Calendar.getInstance()

            for (i in 0 until contacts.count) {

                val date = Contacts.getBirthdayByIndex(contacts, i)

                if (date != null) {
                    val parsedDate = parseDate(date).parsedDate

                    val currentMonth = calendar.get(Calendar.MONTH) + 1
                    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

                    if (parsedDate.monthValue == currentMonth &&
                        parsedDate.dayOfMonth == currentDay
                    ) {
                        val contactName = Contacts.getContactNameByIndex(contacts, i)
                        val contactId = Contacts.getContactIdByIndex(contacts, i)
                        val title = "$contactName hat Geburtstag"

                        if (contactName != null && contactId != null) {
                            showNotification(title, contactId)
                        }
                    }
                }
            }
        }
    }

}
