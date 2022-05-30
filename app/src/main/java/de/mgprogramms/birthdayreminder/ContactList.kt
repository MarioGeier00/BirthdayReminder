package de.mgprogramms.birthdayreminder

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgprogramms.birthdayreminder.birthday.BirthdayData
import com.mgprogramms.birthdayreminder.birthday.BirthdayProviderFactory
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun ContactList() {
    val context = LocalContext.current

    val birthdayProvider = remember { BirthdayProviderFactory.buildProvider(context.applicationContext) }
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        val birthdays = birthdayProvider.getBirthdays()
        birthdays.sortBy { birthday -> birthday.daysUntilNextBirthday() }
        items(birthdays) { birthday ->
            ContactBirthdayItem(birthday)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactBirthdayItem(birthday: BirthdayData) {
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
                val days = birthday.daysUntilNextBirthday()
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
