package de.mgprogramms.birthdayreminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mgprogramms.birthdayreminder.notifications.NotificationWorker
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.navigateTo
import de.mgprogramms.birthdayreminder.destinations.*
import de.mgprogramms.birthdayreminder.ui.theme.BirthdayReminderTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (NotificationWorker.enqueueAtAppStartup(applicationContext)) {
            // enqueueSelf worker in order to activate the service right after
            // the user has installed and opened the app
            NotificationWorker.enqueueSelf(applicationContext, false);
        }

        setContent {
            BirthdayReminderTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Scaffold(bottomBar = { MainNavigationBar(navController) }) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            navController = navController,
                        )
                    }
                }
            }
        }
    }
}

enum class BottomBarDestination(
    val direction: DirectionDestination,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    Home(HomeDestination, Icons.Filled.Home, R.string.home_screen),
    ContactList(ContactListDestination, Icons.Filled.AccountCircle, R.string.contact_list_screen),
    Settings(SettingsDestination, Icons.Filled.Settings, R.string.settings_screen),
}


@Composable
fun MainNavigationBar(navController: NavController) {
    val currentDestination: Destination? = navController.currentBackStackEntryAsState()
        .value?.navDestination

    NavigationBar {
        BottomBarDestination.values().forEach { destination ->
            NavigationBarItem(
                icon = { Icon(destination.icon, contentDescription = null) },
                label = { Text(stringResource(destination.label)) },
                selected = currentDestination == destination.direction,
                onClick = {
                    navController.navigateTo(destination.direction)
                },
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    BirthdayReminderTheme {
        val navController = rememberNavController()
        Scaffold(bottomBar = { MainNavigationBar(navController) }) {
            DestinationsNavHost(navGraph = NavGraphs.root, navController = navController)
        }
    }
}
