package de.mgprogramms.birthdayremindr.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.mgprogramms.birthdayremindr.R
import de.mgprogramms.birthdayremindr.ui.theme.BirthdayRemindrTheme

class NotificationHistoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BirthdayRemindrTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(Modifier.fillMaxSize()) {
                        Text(stringResource(R.string.notification_history), modifier = Modifier.padding(22.dp, 20.dp))
                        NotificationHistory()
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationHistory() {
    val notificationList = remember { emptyList<String>() }

    LazyRow(Modifier.fillMaxWidth()) {
        items(notificationList) { notification ->
            Text(notification, Modifier.padding(22.dp, 14.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationHistoryPreview() {
    BirthdayRemindrTheme {
        Column {
            Text(stringResource(R.string.notification_history), modifier = Modifier.padding(22.dp, 14.dp))
            NotificationHistory()
        }
    }
}
