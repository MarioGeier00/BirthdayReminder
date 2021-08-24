package com.mgprogramms.birthdayreminder

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
import java.sql.Time
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.util.*

class BirthdayNotificationWorker @RequiresApi(Build.VERSION_CODES.O) constructor(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(
    appContext,
    workerParams
) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val sharedPref = applicationContext.getSharedPreferences(
            "BirthdayReminderNotifierWorker",
            Context.MODE_PRIVATE
        );

        if (!sharedPref.getBoolean("notified", false)) {
            showNotification("BirthdayReminderNotifierWorker started")
            with(sharedPref.edit()) {
                putBoolean("notified", true)
                apply()
            }
        }

        notifyAboutBirthdays(applicationContext)

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStopped() {
        showNotification("BirthdayReminderNotifierWorker stopped")
        super.onStopped()
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun enqueueSelf(context: Context, restart: Boolean = false) {
            if (!isActivated(context)) {
                return
            }

            var delayDuration = Duration.between(LocalTime.now(), LocalTime.of(9, 30))
            if (delayDuration.isNegative) {
                delayDuration = delayDuration.plusDays(1)
            }

            val notificationWork =
                PeriodicWorkRequestBuilder<BirthdayNotificationWorker>(
                    Duration.ofDays(1),
                    Duration.ofMillis(PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS)
                ).setInitialDelay(delayDuration).build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "BirthdayReminderNotifierWorker",
                    if (restart) ExistingPeriodicWorkPolicy.REPLACE else ExistingPeriodicWorkPolicy.KEEP,
                    notificationWork
                )
        }

        fun dequeueSelf(context: Context) {
            if (isActivated(context)) {
                return
            }

            WorkManager.getInstance(context).cancelUniqueWork("BirthdayReminderNotifierWorker");
        }

        fun isActivated(context: Context): Boolean {
            val sharedPref = context.getSharedPreferences(
                "BirthdayReminderNotifierWorker",
                Context.MODE_PRIVATE
            );
            return sharedPref.getBoolean("active", false);
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun updateState(context: Context, state: Boolean) {
            val sharedPref = context.getSharedPreferences(
                "BirthdayReminderNotifierWorker",
                Context.MODE_PRIVATE
            );
            with(sharedPref.edit()) {
                putBoolean("active", state)
                putBoolean("notified", false)
                apply()
            }

            if (state) {
                enqueueSelf(context, true)
            } else {
                dequeueSelf(context);
            }
        }
    }

    val CHANNEL_ID = "BirthdayReminderNotifier"

    val NOTIFICATION_ID_DEFAULT = -2;
    val NOTIFICATION_ID_BIRTHDAY = -1;

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(text: String, notificationId: Int = NOTIFICATION_ID_DEFAULT) {
        createNotificationChannel();

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
                        putExtra(NOTIFICATION_ID, notificationId)
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun notifyAboutBirthdays(context: Context) {
        val contacts = getContacts(context)
        if (contacts != null) {
            val calendar = Calendar.getInstance()

            for (i in 0 until contacts.count) {

                val date = getBirthdayByIndex(contacts, i)

                if (date != null) {
                    val parsedDate = parseDate(date).parsedDate

                    val currentMonth = calendar.get(Calendar.MONTH) + 1
                    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

                    if (parsedDate.monthValue == currentMonth &&
                        parsedDate.dayOfMonth == currentDay
                    ) {
                        val contactName = getContactNameByIndex(contacts, i)
                        val contactId = getContactIdByIndex(contacts, i);
                        val title = "$contactName hat Geburtstag"
                        showNotification(
                            title,
                            if (contactId !== null) contactId else NOTIFICATION_ID_BIRTHDAY
                        )
                    }
                }
            }
        }
    }

}