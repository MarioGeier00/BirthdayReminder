package de.mgprogramms.birthdayreminder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import de.mgprogramms.birthdayreminder.providers.BirthdayContactsProvider
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun ContactList() {
    val context = LocalContext.current

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.READ_CONTACTS)
    )
    LifecyclePermissionRequest(permissionsState)

    if (permissionsState.allPermissionsGranted) {

        val birthdays = remember {
            BirthdayContactsProvider(context).getBirthdayContacts()
                .toMutableList()
                .also { it.sortBy { birthday -> birthday.daysUntilBirthday } }
        }

        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(birthdays) {
                ContactBirthdayItem(it)
            }
        }
    } else {
        Text(
            stringResource(R.string.no_permission_contact_list),
            modifier = Modifier.fillMaxSize().padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactBirthdayItem(birthday: BirthdayContact) {

    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxSize()
            .combinedClickable(
                onClick = {
                    showContactDetail(birthday.id, context)
                },
                onLongClick = {
                    showEditContact(birthday.id, context)
                },
            )
            .padding(PaddingValues(horizontal = 22.dp, vertical = 18.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val days = birthday.daysUntilBirthday
                Text(days.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                if (days == 1L) {
                    Text("Tag")
                } else {
                    Text("Tage")
                }
            }
            Spacer(Modifier.requiredWidth(16.dp))
            Text(birthday.name)
        }
        Text(birthday.friendlyBirthdate())
    }
}

fun BirthdayContact.friendlyBirthdate(): String {
    return if (birthDate.yearless) {
        birthDate.parsedDate.format(DateTimeFormatter.ofPattern("dd. MMMM"))
    } else {
        birthDate.parsedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
    }
}

private fun showEditContact(contactId: Int, context: Context) {
    val intent = Intent(Intent.ACTION_EDIT)
    val uri: Uri = Uri.withAppendedPath(
        ContactsContract.Contacts.CONTENT_URI,
        contactId.toString()
    )
    intent.data = uri
    context.startActivity(intent)
}

private fun showContactDetail(contactId: Int, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW)
    val uri: Uri = Uri.withAppendedPath(
        ContactsContract.Contacts.CONTENT_URI,
        contactId.toString()
    )
    intent.data = uri
    context.startActivity(intent)
}
