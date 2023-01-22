package de.mgprogramms.birthdayremindr.ui

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ramcosta.composedestinations.annotation.Destination
import de.mgprogramms.birthdayremindr.R
import de.mgprogramms.birthdayremindr.models.BirthdayContact
import de.mgprogramms.birthdayremindr.providers.BirthDate
import de.mgprogramms.birthdayremindr.providers.NextBirthdayProvider
import java.time.LocalDate


@OptIn(ExperimentalPermissionsApi::class)
@Destination(start = true)
@Composable
fun Home() {
    val context = LocalContext.current
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.READ_CONTACTS)
    )
    LifecyclePermissionRequest(permissionsState)

    NextBirthday(permissionsState.allPermissionsGranted) {
        NextBirthdayProvider(context).getNextBirthdays().firstOrNull()
    }
}

@Composable
fun NextBirthday(allPermissionsGranted: Boolean, nextBirthdayProvider: () -> BirthdayContact?) {
    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (allPermissionsGranted) {
            val nextBirthday = remember {
                nextBirthdayProvider()
            }
            if (nextBirthday == null) {
                Text(
                    stringResource(R.string.birthday_not_found),
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize()
                )
            } else {
                NextBirthdayText(nextBirthday.daysUntilBirthday, nextBirthday.name)
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
fun NextBirthdayText(daysUntilNextBirthday: Long, nextBirthdayName: String) {
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


@Preview(showBackground = true)
@Composable
fun HomePreview() {
    NextBirthday(true) {
        BirthdayContact(1, BirthDate(LocalDate.now(), false), 2, "Max Mustermann")
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreviewNoPermissions() {
    NextBirthday(false) {
        BirthdayContact(1, BirthDate(LocalDate.now(), false), 2, "Max Mustermann")
    }
}