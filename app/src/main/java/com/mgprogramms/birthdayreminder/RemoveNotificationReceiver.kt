package com.mgprogramms.birthdayreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import java.time.LocalDate


class RemoveNotificationReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.extras?.get(NOTIFICATION_ID) as Int

        with(NotificationManagerCompat.from(context)) {
            cancel(notificationId)
        }

        val sharedPref = getSharedPref(context)

        if (sharedPref != null) {
            with(sharedPref.edit()) {
                putString(notificationId.toString(), LocalDate.now().toString())
                apply()
            }
        }
    }

    companion object {
        const val NOTIFICATION_ID = "notificationId"
        private const val IS_ACTIVATED = "isActivated"


        fun getSharedPref(context: Context): SharedPreferences? {
            return context.getSharedPreferences(
                "BirthdayReminderClosedNotifications",
                Context.MODE_PRIVATE
            )
        }

        fun isActivated(context: Context): Boolean {
            val sharedPref = getSharedPref(context)
            if (sharedPref != null) {
                return sharedPref.getBoolean(IS_ACTIVATED, false)
            }
            return false
        }

        fun setActivatedState(context: Context, activated: Boolean) {
            val sharedPref = getSharedPref(context)

            if (sharedPref != null) {
                with(sharedPref.edit()) {
                    if (!activated) {
                        clear()
                    }
                    putBoolean(IS_ACTIVATED, activated)
                    apply()
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun hasBeenRemovedToday(context: Context, notificationId: Int): Boolean {
            val sharedPref = getSharedPref(context)
            if (sharedPref != null) {
                val dismissDate = sharedPref.getString(notificationId.toString(), null)
                val today = LocalDate.now().toString()

                return dismissDate == today
            }
            return false
        }
    }
}
