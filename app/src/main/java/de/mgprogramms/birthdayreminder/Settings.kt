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
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mgprogramms.birthdayreminder.birthday.BirthdayProviderFactory
import com.mgprogramms.birthdayreminder.notifications.NotificationWorker
import com.mgprogramms.birthdayreminder.notifications.RemoveNotificationReceiver
import com.ramcosta.composedestinations.annotation.Destination
import de.mgprogramms.birthdayreminder.ui.theme.BirthdayReminderTheme


@Destination
@Composable
fun Settings() {
    val context = LocalContext.current

    val showNotificationState = remember { mutableStateOf(NotificationWorker.isActivated(context)) }
    val removeNotificationState =
        remember { mutableStateOf(RemoveNotificationReceiver.isActivated(context)) }

    val serviceOnAppStartState =
        remember { mutableStateOf(NotificationWorker.enqueueAtAppStartup(context)) }
    val useTestPersonState =
        remember { mutableStateOf(BirthdayProviderFactory.shouldUseTestPerson(context)) }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        SettingsSwitch(
            showNotificationState,
            {
                NotificationWorker.updateState(context, it)
                showNotificationState.value = NotificationWorker.isActivated(context)
            },
            stringResource(R.string.settings_birthday_notification_title),
            stringResource(R.string.settings_birthday_notification_description),
        )

        val durationUntilNextNotification = remember { NotificationWorker.getDurationUntilNextNotification() }

        val minutes = if (durationUntilNextNotification.toHours() > 0) {
            durationUntilNextNotification.toMinutes() % (durationUntilNextNotification.toHours() * 60)
        } else {
            durationUntilNextNotification.toMinutes()
        }
        Row(Modifier.padding(22.dp, 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Info, "info")
            Spacer(Modifier.width(8.dp))
            Text(
                String.format(
                    stringResource(R.string.settings_next_notification),
                    durationUntilNextNotification.toHours(),
                    minutes
                ),
            )
        }

        SettingsSwitch(
            removeNotificationState,
            {
                RemoveNotificationReceiver.setActivatedState(context, it)
                removeNotificationState.value = RemoveNotificationReceiver.isActivated(context)
            },
            stringResource(R.string.settings_removable_notification_title),
        )
        SettingsSwitch(
            serviceOnAppStartState,
            {
                NotificationWorker.setEnqueueAtAppStartup(context, it)
                serviceOnAppStartState.value = NotificationWorker.enqueueAtAppStartup(context)
            },
            stringResource(R.string.settings_start_service_on_app_start),
        )
        SettingsSwitch(
            useTestPersonState,
            {
                BirthdayProviderFactory.setUseTestPerson(context, it)
                useTestPersonState.value = BirthdayProviderFactory.shouldUseTestPerson(context)
            },
            stringResource(R.string.settings_use_test_person_title),
            stringResource(R.string.settings_use_test_person_description),
        )
        Spacer(Modifier.height(18.dp))
        Text(stringResource(R.string.settings_test_notification_worker), Modifier.clickable {
            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>().build()
            WorkManager.getInstance(context).enqueue(workRequest)
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
