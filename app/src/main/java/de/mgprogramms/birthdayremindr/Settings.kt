package de.mgprogramms.birthdayremindr

import android.Manifest
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.ramcosta.composedestinations.annotation.Destination
import de.mgprogramms.birthdayremindr.notifications.BirthdayNotification
import de.mgprogramms.birthdayremindr.models.toBirthdayContact
import de.mgprogramms.birthdayremindr.providers.AlarmProvider
import de.mgprogramms.birthdayremindr.providers.ContactsProvider
import de.mgprogramms.birthdayremindr.providers.NextBirthdayProvider
import de.mgprogramms.birthdayremindr.ui.theme.BirthdayRemindrTheme
import java.time.Duration
import java.time.LocalTime


@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun Settings() {
    val context = LocalContext.current

    val notificationPermissionState =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            null
        }

    val alarmProvider = remember { AlarmProvider(context) }
    var hasAnyAlarms by remember { mutableStateOf(alarmProvider.hasAnyAlarms()) }
    val showNotificationState by remember{
        derivedStateOf {
            hasAnyAlarms && notificationPermissionState?.status?.isGranted != false
        }
    }


    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        SettingsSwitch(
            showNotificationState,
            {
                if (it && notificationPermissionState?.status?.isGranted == false) {
                    if (notificationPermissionState.status.shouldShowRationale) {
                        Toast.makeText(context, R.string.no_permission_notification, Toast.LENGTH_LONG).show()
                    } else {
                        notificationPermissionState.launchPermissionRequest()
                    }
                }
                with(AlarmProvider(context)) {
                    removeBirthdayAlarms()
                    if (it) {
                        NextBirthdayProvider(context).getNextBirthdays()
                            .forEach {
                                setAlarmForBirthday(it)
                            }
                    }
                    hasAnyAlarms = hasAnyAlarms()
                }
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
        Text(stringResource(R.string.settings_test_notification_direct), Modifier.clickable {
            val notification = BirthdayNotification(
                context,
                ContactsProvider(context).getContacts().find { it.name == "Mama" }!!.toBirthdayContact()
            )
            notification.show(context, notification.create(), notification.birthdayData.id)
        }.padding(22.dp, 16.dp).fillMaxWidth())

        Text(stringResource(R.string.notification_history), Modifier.clickable {
            context.startActivity(Intent(context, NotificationHistoryActivity::class.java))
        }.padding(22.dp, 16.dp).fillMaxWidth())
    }
}

@Composable
fun SettingsSwitch(state: Boolean, stateChange: (Boolean) -> Unit, title: String, description: String? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.clickable { stateChange(!state) }
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
            checked = state,
            onCheckedChange = stateChange
        )
    }
}

@Preview
@Composable
fun SettingsPreview() {
    BirthdayRemindrTheme {
        Surface {
            Settings()
        }
    }
}
