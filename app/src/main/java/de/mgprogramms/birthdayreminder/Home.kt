package de.mgprogramms.birthdayreminder

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ramcosta.composedestinations.annotation.Destination
import de.mgprogramms.birthdayreminder.models.BirthdayContact
import de.mgprogramms.birthdayreminder.providers.NextBirthdayProvider


fun getNextBirthday(birthdays: List<BirthdayContact>): BirthdayContact? {
    if (birthdays.isNotEmpty()) {
        var closestBirthday = birthdays.first()
        for (birthday in birthdays) {
            if (birthday.daysUntilBirthday < closestBirthday.daysUntilBirthday) {
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
    LifecyclePermissionRequest(permissionsState)

    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (permissionsState.allPermissionsGranted) {
            val nextBirthday = remember {
                NextBirthdayProvider(context).getNextBirthdays().firstOrNull()
            }
            if (nextBirthday == null) {
                Text(
                    stringResource(R.string.birthday_not_found),
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize()
                )
            } else {
                NextBirthday(nextBirthday.daysUntilBirthday, nextBirthday.name)
            }
        } else {
            Text(
                stringResource(R.string.no_permission_contact_list),
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
