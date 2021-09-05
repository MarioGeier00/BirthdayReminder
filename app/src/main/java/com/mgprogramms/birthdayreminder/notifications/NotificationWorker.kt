package com.mgprogramms.birthdayreminder.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mgprogramms.birthdayreminder.birthday.BirthdayProvider
import com.mgprogramms.birthdayreminder.birthday.BirthdayProviderFactory

class NotificationWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    private lateinit var birthdayProvider: BirthdayProvider

    override fun doWork(): Result {
        birthdayProvider = BirthdayProviderFactory.buildProvider(applicationContext)

        for (birthday in birthdayProvider.getBirthdays()) {
            if (birthday.hasBirthday()) {
                BirthdayNotification.show(applicationContext, birthday)
            }
        }

        return Result.success()
    }
}
