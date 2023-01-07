package de.mgprogramms.birthdayremindr.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.mgprogramms.birthdayremindr.notifications.BirthdayNotification
import de.mgprogramms.birthdayremindr.models.toBirthdayContact
import de.mgprogramms.birthdayremindr.providers.AlarmProvider
import de.mgprogramms.birthdayremindr.providers.ContactsProvider
import de.mgprogramms.birthdayremindr.providers.NextBirthdayProvider


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
