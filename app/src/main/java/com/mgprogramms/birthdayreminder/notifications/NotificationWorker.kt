package com.mgprogramms.birthdayreminder.notifications

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import com.mgprogramms.birthdayreminder.birthday.BirthdayProvider
import com.mgprogramms.birthdayreminder.birthday.BirthdayProviderFactory
import java.time.Duration
import java.time.LocalTime

class NotificationWorker(val context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {
    private lateinit var birthdayProvider: BirthdayProvider

    override suspend fun doWork(): Result {

        birthdayProvider = BirthdayProviderFactory.buildProvider(context)

        for (birthday in birthdayProvider.getBirthdays()) {
            if (birthday.hasBirthday()) {
                if (!RemoveNotificationReceiver.isActivated(context) ||
                    !RemoveNotificationReceiver.hasBeenRemovedToday(
                        context,
                        birthday.id
                    )
                ) {
                    val notification = BirthdayNotification.create(context, birthday)
                    BirthdayNotification.show(context, notification, birthday.id)
//                    setForeground(ForegroundInfo(birthday.id, notification))
                }
            }
        }

//        Thread.sleep(50000)

        return Result.success()
    }

    companion object {
        private const val WORKER_ID = "NotificationWorker"


        @RequiresApi(Build.VERSION_CODES.O)
        fun getDurationUntilNextNotification(): Duration {
            var duration = Duration.between(LocalTime.now(), LocalTime.of(9, 30))
            while (duration.isNegative) {
                duration = duration.plusHours(2)
            }
            return duration.minusHours(duration.toHours() / 2)
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

            val delayDuration = getDurationUntilNextNotification()

            val notificationWork =
                PeriodicWorkRequestBuilder<NotificationWorker>(
                    Duration.ofHours(2),
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
}
