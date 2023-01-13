package de.mgprogramms.birthdayremindr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.navigate
import de.mgprogramms.birthdayremindr.destinations.ContactListDestination
import de.mgprogramms.birthdayremindr.destinations.DirectionDestination
import de.mgprogramms.birthdayremindr.destinations.HomeDestination
import de.mgprogramms.birthdayremindr.destinations.SettingsDestination
import de.mgprogramms.birthdayremindr.ui.theme.BirthdayRemindrTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BirthdayRemindrTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    Scaffold(bottomBar = { MainNavigationBar(navController) }) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            navController = navController,
                            modifier = Modifier.padding(it),
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
    val currentDestination = navController.currentBackStackEntryAsState()
        .value?.appDestination()

    NavigationBar {
        BottomBarDestination.values().forEach { destination ->
            NavigationBarItem(
                icon = { Icon(destination.icon, contentDescription = null) },
                label = { Text(stringResource(destination.label)) },
                selected = currentDestination == destination.direction,
                onClick = {
                    navController.navigate(destination.direction) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // re-selecting the same item
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }
                },
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    BirthdayRemindrTheme {
        val navController = rememberNavController()
        Scaffold(bottomBar = { MainNavigationBar(navController) }) {
            DestinationsNavHost(
                navGraph = NavGraphs.root,
                navController = navController,
                modifier = Modifier.padding(it),
            )
        }
    }
}
