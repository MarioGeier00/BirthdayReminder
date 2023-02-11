package de.mgprogramms.birthdayremindr.ui.contactdetail

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import de.mgprogramms.birthdayremindr.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresentDialog(onDismiss: () -> Unit, onNewPresent: (present: String) -> Unit) {
    var textState by remember { mutableStateOf(TextFieldValue()) }
    val focusRequester = remember { FocusRequester() }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.new_present))
        },
        text = {
            TextField(textState, { textState = it }, Modifier.focusRequester(focusRequester))
        },
        confirmButton = {
            Button({ onNewPresent(textState.text) }) {
                Text(stringResource(R.string.add))
            }
        })
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}