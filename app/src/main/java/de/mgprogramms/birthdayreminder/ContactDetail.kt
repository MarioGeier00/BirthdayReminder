package de.mgprogramms.birthdayreminder

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ramcosta.composedestinations.annotation.Destination
import de.mgprogramms.birthdayreminder.models.toBirthdayContact
import de.mgprogramms.birthdayreminder.providers.ContactsProvider
import de.mgprogramms.birthdayreminder.providers.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "contact_details")

data class Present(
    val name: String,
    var done: Boolean,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
        Column(Modifier.fillMaxSize().padding(22.dp, 18.dp)) {
            Text(contact.name, fontSize = 48.sp)
            Spacer(Modifier.height(8.dp))
            Text("Geburtstag in ${contact.daysUntilBirthday} Tagen")

            Spacer(Modifier.height(24.dp))
            Text("Geschenk-Ideen")
            Spacer(Modifier.height(8.dp))

            val presentsPreference = remember { stringSetPreferencesKey("presents_$contactId") }
            val presents = remember {
                runBlocking {
                    context.dataStore.data.first()[presentsPreference] ?: setOf()
                }.map { Present(it, false) }.toMutableList()
            }

            val donePresentsPreference = remember { stringSetPreferencesKey("presents_done_$contactId") }
            val donePresents = remember {
                runBlocking {
                    context.dataStore.data.first()[donePresentsPreference] ?: setOf()
                }.map { Present(it, true) }.toMutableList()
            }

            val scrollState = rememberLazyListState()

            LazyColumn(state = scrollState, modifier = Modifier.fillMaxHeight(0.8f)) {
                items(presents) { present ->
                    ListItem({
                        Text(present.name)
                    }, leadingContent = {
                        var state by remember { mutableStateOf(present.done) }
                        Checkbox(state, {
                            state = !state
                            present.done = state
                            context.storePresents(presentsPreference, presents, donePresentsPreference, donePresents)
                        })
                    }, modifier = Modifier.fillMaxSize().combinedClickable(onClick = {}, onLongClick = {
                        presents.remove(present)
                        context.storePresents(presentsPreference, presents, donePresentsPreference, donePresents)
                    }))
                }
                item {
                    ListItem({ Text("Erledigt") })
                }
                items(donePresents) { present ->
                    ListItem({
                        Text(present.name)
                    }, leadingContent = {
                        var state by remember { mutableStateOf(present.done) }
                        Checkbox(state, {
                            state = !state
                            present.done = state
                            context.storePresents(presentsPreference, presents, donePresentsPreference, donePresents)
                        })
                    }, trailingContent = {
                        IconButton(onClick = {
                            donePresents.remove(present)
                            context.storePresents(presentsPreference, presents, donePresentsPreference, donePresents)

                        }) {
                            Icon(Icons.Default.Delete, "Remove")
                        }
                    })
                }
            }
            var textState by remember { mutableStateOf(TextFieldValue()) }
            var openDialog by remember { mutableStateOf(false) }
            ExtendedFloatingActionButton(
                onClick = {
                    openDialog = true
                },
                icon = {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add"
                    )
                },
                text = { Text("Present") },
                modifier = Modifier.align(Alignment.End),

                )
            Spacer(Modifier.height(56.dp))

            if (openDialog) {
                AlertDialog(onDismissRequest = {},
                    title = {
                        Text("Neue Geschenk-Idee")
                    },
                    text = {
                        TextField(
                            textState,
                            { textState = it }
                        )
                    },
                    confirmButton = {
                        Button({
                            presents.add(Present(textState.text, false))
                            context.storePresents(presentsPreference, presents, donePresentsPreference, donePresents)
                            openDialog = false
                        }) {
                            Text("Hinzufügen")
                        }
                    })
            }
        }
    }
}

fun Context.storePresents(
    preference: Preferences.Key<Set<String>>,
    presents: List<Present>,
    donePresentsPreference: Preferences.Key<Set<String>>,
    donePresents: MutableList<Present>,
) {
    runBlocking {
        val combinedPresents = presents.plus(donePresents)
        dataStore.edit { store ->
            store[preference] =
                combinedPresents
                    .filter { !it.done }
                    .map { it.name }
                    .toSet()
            store[donePresentsPreference] =
                combinedPresents
                    .filter { it.done }
                    .map { it.name }
                    .toSet()
        }
    }
}
