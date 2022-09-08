package de.mgprogramms.birthdayreminder

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import de.mgprogramms.birthdayreminder.models.toBirthdayContact
import de.mgprogramms.birthdayreminder.providers.AlarmProvider
import de.mgprogramms.birthdayreminder.providers.ContactsProvider
import de.mgprogramms.birthdayreminder.providers.NextBirthdayProvider
import de.mgprogramms.birthdayreminder.ui.theme.BirthdayReminderTheme
import java.time.Duration
import java.time.LocalTime


@Destination
@Composable
fun Settings() {
    val context = LocalContext.current

    val alarmProvider = remember { AlarmProvider(context) }


    val showNotificationState = remember { mutableStateOf(alarmProvider.hasAnyAlarms()) }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        SettingsSwitch(
            showNotificationState,
            {
                // TODO: Add notification permission request
                with(AlarmProvider(context)) {
                    if (alarmProvider.hasAnyAlarms()) {
                        removeBirthdayAlarms()
                    } else {
                        NextBirthdayProvider(context).getNextBirthdays()
                            .forEach {
                                setAlarmForBirthday(it)
                            }
                    }
                }
                showNotificationState.value = alarmProvider.hasAnyAlarms()
            },
            stringResource(R.string.settings_birthday_notification_title),
            stringResource(R.string.settings_birthday_notification_description),
        )

        val nextBirthday = remember { NextBirthdayProvider(context).getNextBirthdaysExceptToday().first() }
        val duration = remember {
            Duration.between(
                LocalTime.now(),
                LocalTime.of(0, 0)
            ).plusHours(nextBirthday.daysUntilBirthday * 24)
        }
        val minutes = if (duration.toHours() > 0) {
            duration.toMinutes() % (duration.toHours() * 60)
        } else {
            duration.toMinutes()
        }
        Row(Modifier.padding(22.dp, 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Info, "info")
            Spacer(Modifier.width(8.dp))
            Text(
                String.format(
                    stringResource(R.string.settings_next_notification),
                    duration.toHours(),
                    minutes
                ),
            )
        }

        Spacer(Modifier.height(18.dp))
        Text(stringResource(R.string.settings_test_notification_alarm), Modifier.clickable {
            AlarmProvider(context).setAlarmForBirthday(
                ContactsProvider(context).getContacts().first().toBirthdayContact()
            )
        }.padding(22.dp, 16.dp).fillMaxWidth())

        Text(stringResource(R.string.notification_history), Modifier.clickable {
            context.startActivity(Intent(context, NotificationHistoryActivity::class.java))
        }.padding(22.dp, 16.dp).fillMaxWidth())
    }
}

@Composable
fun SettingsSwitch(state: State<Boolean>, stateChange: (Boolean) -> Unit, title: String, description: String? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.clickable { stateChange(!state.value) }
            .padding(22.dp, 16.dp)
            .fillMaxWidth(),
    ) {
        Column(Modifier.fillMaxWidth(0.86f)) {
            Text(title, fontSize = 22.sp)
            if (description != null) {
                Spacer(Modifier.height(0.dp))
                Text(description)
            }
        }
        Switch(
            checked = state.value,
            onCheckedChange = stateChange
        )
    }
}

@Preview
@Composable
fun SettingsPreview() {
    BirthdayReminderTheme {
        Surface {
            Settings()
        }
    }
}
