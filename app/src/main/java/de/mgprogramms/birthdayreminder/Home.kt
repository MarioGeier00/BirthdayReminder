package de.mgprogramms.birthdayreminder

import android.Manifest
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mgprogramms.birthdayreminder.birthday.BirthdayData
import com.mgprogramms.birthdayreminder.birthday.BirthdayProviderFactory
import com.ramcosta.composedestinations.annotation.Destination


fun getNextBirthday(birthdays: Array<BirthdayData>): BirthdayData? {
    if (birthdays.isNotEmpty()) {
        var closestBirthday: BirthdayData = birthdays.first()
        for (birthday in birthdays) {
            if (birthday.daysUntilNextBirthday() < closestBirthday.daysUntilNextBirthday()) {
                closestBirthday = birthday
            }
        }
        return closestBirthday
    }

    return null
}


@OptIn(ExperimentalPermissionsApi::class)
@Destination(start = true)
@Composable
fun Home() {
    val context = LocalContext.current
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.READ_CONTACTS)
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    permissionsState.launchMultiplePermissionRequest()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (permissionsState.allPermissionsGranted) {
            val birthdays by remember {
                mutableStateOf(
                    BirthdayProviderFactory.buildProvider(context.applicationContext).getBirthdays()
                )
            }
            val nextBirthday by remember { mutableStateOf(getNextBirthday(birthdays)) }
            val nextBirthdayValue = nextBirthday
            if (nextBirthdayValue == null) {
                Text(
                    stringResource(R.string.birthday_not_found),
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize()
                )
            } else {
                NextBirthday(nextBirthdayValue.daysUntilNextBirthday(), nextBirthdayValue.name)
            }
        } else {
            Text(
                "Zugriff auf deine Kontraktliste wurde abgelehnt.",
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize()
            )
        }
    }

}

@Composable
fun NextBirthday(daysUntilNextBirthday: Long, nextBirthdayName: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(daysUntilNextBirthday.toString(), fontSize = 96.sp, fontWeight = FontWeight.Bold)
        Text(
            (if (daysUntilNextBirthday == 1L)
                stringResource(R.string.day)
            else
                stringResource(R.string.days))
                    + "\n" +
                    stringResource(R.string.until_birthday_of) + " " + nextBirthdayName,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )
    }
}
