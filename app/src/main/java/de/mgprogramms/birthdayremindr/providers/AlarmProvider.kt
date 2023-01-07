package de.mgprogramms.birthdayreminder.providers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import de.mgprogramms.birthdayreminder.models.BirthdayContact
import de.mgprogramms.birthdayreminder.receivers.AlarmReceiver
import de.mgprogramms.birthdayreminder.receivers.AlarmReceiverContactIdKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class AlarmProvider(val context: Context) {
    val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun setAlarmForBirthday(birthdayContact: BirthdayContact) {
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, birthdayContact.daysUntilBirthday.toInt())
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        val intent = Intent(context, AlarmReceiver::class.java).also {
            it.putExtra(AlarmReceiverContactIdKey, birthdayContact.id)
            it.flags = Intent.FLAG_RECEIVER_FOREGROUND
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                birthdayContact.id,
                intent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)

        val preference = intPreferencesKey(birthdayContact.id.toString())
        runBlocking {
            context.dataStore.edit { settings ->
                val currentCounterValue = settings[preference] ?: 0
                settings[preference] = currentCounterValue + 1
            }
        }
    }

    fun removeBirthdayAlarms() {
        runBlocking {
            context.dataStore.edit { settings ->
                settings.asMap().keys.forEach {
                    val intent = Intent(context, AlarmReceiver::class.java)

                    val pendingIntent =
                        PendingIntent.getBroadcast(context, it.name.toInt(), intent, PendingIntent.FLAG_MUTABLE)

                    alarmManager.cancel(pendingIntent)
                }
                settings.clear()
            }
        }
    }

    fun hasAnyAlarms(): Boolean {
        return runBlocking { context.dataStore.data.first() }.asMap().keys.any()
    }
}
