package de.mgprogramms.birthdayreminder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.mgprogramms.birthdayreminder.models.toBirthdayContact
import de.mgprogramms.birthdayreminder.notifications.BirthdayNotification
import de.mgprogramms.birthdayreminder.providers.AlarmProvider
import de.mgprogramms.birthdayreminder.providers.ContactsProvider
import de.mgprogramms.birthdayreminder.providers.NextBirthdayProvider


const val AlarmReceiverContactIdKey = "contact_id"

class AlarmReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            // reset alarms
            with(AlarmProvider(context)) {
                NextBirthdayProvider(context).getNextBirthdays()
                    .forEach {
                        setAlarmForBirthday(it)
                    }
            }

        } else {

            // an alarm for today was received
            // therefore only the alarms for the next day or later must be set
            with(AlarmProvider(context)) {
                NextBirthdayProvider(context).getNextBirthdaysExceptToday()
                    .forEach {
                        setAlarmForBirthday(it)
                    }
            }

            // get contact id from scheduled birthday alarm
            intent.extras?.getInt(AlarmReceiverContactIdKey)
                ?.let { ContactsProvider(context).getContactById(it) }
                ?.also {
                    val notification = BirthdayNotification(context, it.toBirthdayContact())
                    notification.show(context, notification.create(), it.id)
                }
        }
    }
}
