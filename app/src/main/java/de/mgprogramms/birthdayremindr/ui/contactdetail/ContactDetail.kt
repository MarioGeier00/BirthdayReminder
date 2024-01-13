package de.mgprogramms.birthdayremindr.ui.contactdetail

import Presents
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.ramcosta.composedestinations.annotation.Destination
import de.mgprogramms.birthdayremindr.models.BirthdayContact
import de.mgprogramms.birthdayremindr.models.Contact
import de.mgprogramms.birthdayremindr.models.toBirthdayContact
import de.mgprogramms.birthdayremindr.providers.ContactsProvider
import de.mgprogramms.birthdayremindr.providers.presents.PresentsSerializer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@Destination
@Composable
fun ContactDetail(
    contactId: Int
) {
    val context = LocalContext.current
    val contact = remember {
        ContactsProvider(context).getContactById(contactId)?.toBirthdayContact()
    }

    if (contact != null) {
        ContactDetail(contact)
    }
}

val Context.presentsStore: DataStore<Presents> by dataStore(
    fileName = "presents.pb",
    serializer = PresentsSerializer,
)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactDetail(
    contact: BirthdayContact
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(22.dp, 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(contact.name, fontSize = 48.sp)
        Spacer(Modifier.height(14.dp))

        BirthdayDetailInfo(contact)

        Text("Geschenk-Ideen")


        val allPresents: Presents by context.presentsStore.data.collectAsState(PresentsSerializer.defaultValue)
        val userPresents = allPresents.usersList.find { it.userId == contact.id } ?: Presents.User.getDefaultInstance()

        val groupedPresents = userPresents.presentsList.groupBy { it.done }
        val presents = groupedPresents[false]
        val donePresents = groupedPresents[true]

        val scrollState = rememberLazyListState()

        LazyColumn(state = scrollState, modifier = Modifier.weight(1F, true)) {
            if (presents != null) {
                items(presents) { present ->
                    ListItem({ Text(present.text) }, leadingContent = {
                        Checkbox(present.done, {
                            coroutineScope.launch {
                                updatePresentDoneState(context.presentsStore, contact.id, present, it)
                            }
                        })
                    }, modifier = Modifier.fillMaxSize().combinedClickable(onClick = {}, onLongClick = {
                        runBlocking {
                            removePresent(context.presentsStore, contact.id, present)
                        }
                    }))
                }
            }

            if (donePresents != null) {
                items(donePresents) { present ->
                    ListItem({ Text(present.text) },
                        leadingContent = {
                            Checkbox(present.done, {
                                coroutineScope.launch {
                                    updatePresentDoneState(context.presentsStore, contact.id, present, it)
                                }
                            })
                        },
                        trailingContent = {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    removePresent(context.presentsStore, contact.id, present)
                                }
                            }) {
                                Icon(Icons.Default.Delete, "Remove")
                            }
                        })
                }
            }

        }

        var openDialog by remember { mutableStateOf(false) }
        ExtendedFloatingActionButton(
            onClick = { openDialog = true },
            icon = { Icon(Icons.Filled.Add, contentDescription = "Add") },
            text = { Text("Present") },
            modifier = Modifier.align(Alignment.End),
        )

        if (openDialog) {
            var textState by remember { mutableStateOf(TextFieldValue()) }
            AlertDialog(
                onDismissRequest = {
                    openDialog = false
                },
                title = {
                    Text("Neue Geschenk-Idee")
                },
                text = {
                    TextField(textState, { textState = it })
                },
                confirmButton = {
                    Button({
                        runBlocking {
                            context.presentsStore.updateData { presents ->
                                val userIndex = presents.usersList.indexOfFirst { it.userId == contact.id }

                                val presentBuilder = Presents.User.Present.newBuilder()
                                    .setText(textState.text)
                                    .setDone(false)

                                if (userIndex >= 0) {
                                    presents.toBuilder().setUsers(
                                        userIndex,
                                        Presents.User.newBuilder().addPresents(presentBuilder)
                                    ).build()
                                } else {
                                    presents.toBuilder().addUsers(
                                        Presents.User.newBuilder().setUserId(contact.id).addPresents(presentBuilder)
                                    ).build()
                                }
                            }
                        }
                        openDialog = false
                    }) {
                        Text("Hinzufügen")
                    }
                })
        }
    }
}


suspend fun updatePresentDoneState(
    dataStore: DataStore<Presents>,
    userId: Int,
    present: Presents.User.Present,
    done: Boolean
) {
    dataStore.updateData { presents ->
        val index = presents.usersList[userId].presentsList.indexOf(present)
        presents.toBuilder()
            .setUsers(
                userId,
                presents.usersList[userId].toBuilder().setPresents(index, present.toBuilder().setDone(done))
            ).build()
    }
}

suspend fun removePresent(dataStore: DataStore<Presents>, userId: Int, present: Presents.User.Present) {
    dataStore.updateData { presents ->
        val index = presents.usersList[userId].presentsList.indexOf(present)
        presents.toBuilder()
            .setUsers(
                userId,
                presents.usersList[userId].toBuilder().removePresents(index)
            ).build()
    }
}


@Preview(showBackground = true)
@Composable
fun ContactDetailPreview() {
    ContactDetail(
        Contact(11111, "1992-05-19", "Peter Test")
            .toBirthdayContact()
    )
}
