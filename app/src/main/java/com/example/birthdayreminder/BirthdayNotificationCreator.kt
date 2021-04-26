package com.example.birthdayreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*


@Deprecated("Use BirthdayNotificationWorker instead")
class BirthdayNotificationCreator : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {
        var contacts = getContacts(context)
        if (contacts != null) {
            val calendar = Calendar.getInstance()

            for (i in 0 until contacts.count) {

                var date = getBirthdayByIndex(contacts, i)

                if (date != null) {
                    var parsedDate = parseDate(date).parsedDate

                    var currentMonth = calendar.get(Calendar.MONTH) + 1
                    var currentDay = calendar.get(Calendar.DAY_OF_MONTH)

                    if (parsedDate.monthValue == currentMonth &&
                        parsedDate.dayOfMonth == currentDay
                    ) {
                        var contactName = getContactNameByIndex(contacts, i)
                        var title = "$contactName hat Geburtstag"
                        showNotification(context, title, "")
                    }
                }

            }
        }
    }
}