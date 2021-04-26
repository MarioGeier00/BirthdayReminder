package com.example.birthdayreminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import java.time.Duration
import java.util.*

class BirthdayNotificationWorker @RequiresApi(Build.VERSION_CODES.O) constructor(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(
    appContext,
    workerParams
) {

    var isInitialized = false;

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        if (!isInitialized) {
            showNotification("BirthdayReminderNotifierWorker started")
            isInitialized = true
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
            val notificationWork =
                PeriodicWorkRequestBuilder<BirthdayNotificationWorker>(Duration.ofMinutes(16)).build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "BirthdayReminderNotifierWorker",
                    if (restart) ExistingPeriodicWorkPolicy.REPLACE else ExistingPeriodicWorkPolicy.KEEP,
                    notificationWork
                )
        }
    }

    val CHANNEL_ID = "BirthdayReminderNotifier"

    val NOTIFICATION_ID_DEFAULT = 0;
    val NOTIFICATION_ID_BIRTHDAY = 1;

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(text: String, notificationId: Int = NOTIFICATION_ID_DEFAULT) {
        createNotificationChannel();

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(text)
            .setContentText("BirthdayReminder")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(applicationContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "BirthdayReminder"
            val descriptionText =
                "BirthdayReminder informs you about upcoming birthdays by readying your contacts date of birth"
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
                        showNotification(title, if (contactId !== null) contactId else NOTIFICATION_ID_BIRTHDAY)
                    }
                }
            }
        }
    }

}