package de.mgprogramms.birthdayreminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (NotificationWorker.enqueueAtAppStartup(applicationContext)) {
            // enqueueSelf worker in order to activate the service right after
            // the user has installed and opened the app
            NotificationWorker.enqueueSelf(applicationContext, false);
        }

        setContent {
            BirthdayReminderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background,
                ) {
                    val navController = rememberNavController()
                    Scaffold(bottomBar = { NavigationBar(navController) }) {
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
    Home(HomeDestination, Icons.Default.Home, R.string.home_screen),
    ContactList(ContactListDestination, Icons.Default.AccountCircle, R.string.contact_list_screen),
    Settings(SettingsDestination, Icons.Default.Settings, R.string.settings_screen),
}


@Composable
fun NavigationBar(navController: NavController) {
    val currentDestination: Destination? = navController.currentBackStackEntryAsState()
        .value?.navDestination

    BottomNavigation {
        BottomBarDestination.values().forEach { destination ->
            BottomNavigationItem(
                selected = currentDestination == destination.direction,
                onClick = {
                    navController.navigateTo(destination.direction) {
                        launchSingleTop = true
                    }
                },
                icon = { Icon(destination.icon, contentDescription = stringResource(destination.label)) },
                label = { Text(stringResource(destination.label)) },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = MaterialTheme.colors.onBackground
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    BirthdayReminderTheme {
        val navController = rememberNavController()
        Scaffold(bottomBar = { NavigationBar(navController) }) {
            DestinationsNavHost(navGraph = NavGraphs.root, navController = navController)
        }
    }
}
