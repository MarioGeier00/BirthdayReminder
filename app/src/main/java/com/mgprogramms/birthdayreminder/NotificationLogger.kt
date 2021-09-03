package com.mgprogramms.birthdayreminder

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import java.time.LocalDateTime

class NotificationLogger {
    companion object {

        private const val NOTIFICATIONS = "notifications"
        private fun getSharedPref(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                "NotificationLogger",
                Context.MODE_PRIVATE
            )
        }

        fun getNotifications(context: Context): MutableSet<String> {
            return getSharedPref(context).getStringSet(NOTIFICATIONS, emptySet())!!
        }

        fun addNotification(context: Context, message: String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val dateTime =
                    LocalDateTime.now()

                val notifications = getNotifications(context).toMutableSet().apply {
                    add("[$dateTime] $message")
                }

                with(getSharedPref(context).edit()) {
                    putStringSet(NOTIFICATIONS, notifications)
                    apply()
                }
            }

        }

        fun clear(context: Context) {
            with(getSharedPref(context).edit()) {
                clear()
                apply()
            }
        }

    }
}
