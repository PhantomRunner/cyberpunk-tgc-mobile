package com.example.cyberpunktcg.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cyberpunktcg.R
import com.example.cyberpunktcg.ui.card.CardDetailsScreen
import com.example.cyberpunktcg.ui.collection.CollectionScreen
import com.example.cyberpunktcg.ui.decks.DeckEditorScreen
import com.example.cyberpunktcg.ui.decks.DeckPreviewScreen
import com.example.cyberpunktcg.ui.decks.MyDecksScreen
import com.example.cyberpunktcg.ui.home.HomeScreen
import com.example.cyberpunktcg.ui.scanner.ScannerScreen
import com.example.cyberpunktcg.ui.settings.SettingsScreen

private data class MainDestination(
    val destination: AppDestination,
    @get:DrawableRes val icon: Int,
)

private val mainDestinations = listOf(
    MainDestination(AppDestination.Home, R.drawable.ic_home),
    MainDestination(AppDestination.Collection, R.drawable.ic_collection),
    MainDestination(AppDestination.Decks, R.drawable.ic_decks),
    MainDestination(AppDestination.Scanner, R.drawable.ic_scanner),
    MainDestination(AppDestination.Settings, R.drawable.ic_settings),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanionApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val current = AppDestination.entries.firstOrNull {
        it.route == backStackEntry?.destination?.route
    } ?: AppDestination.Home

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(current.title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    if (current != current.section) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = stringResource(R.string.back),
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                mainDestinations.forEach { item ->
                    NavigationBarItem(
                        selected = current.section == item.destination,
                        onClick = {
                            if (current.section != item.destination) {
                                navController.navigate(item.destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(painterResource(item.icon), contentDescription = null)
                        },
                        label = { Text(stringResource(item.destination.title)) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            composable(AppDestination.Home.route) { HomeScreen() }
            composable(AppDestination.Collection.route) {
                CollectionScreen(
                    onOpenCard = { navController.navigate(AppDestination.CardDetails.route) },
                )
            }
            composable(AppDestination.CardDetails.route) { CardDetailsScreen() }
            composable(AppDestination.Decks.route) {
                MyDecksScreen(
                    onOpenDeck = { navController.navigate(AppDestination.DeckEditor.route) },
                )
            }
            composable(AppDestination.DeckEditor.route) {
                DeckEditorScreen(
                    onOpenPreview = { navController.navigate(AppDestination.DeckPreview.route) },
                )
            }
            composable(AppDestination.DeckPreview.route) { DeckPreviewScreen() }
            composable(AppDestination.Scanner.route) { ScannerScreen() }
            composable(AppDestination.Settings.route) { SettingsScreen() }
        }
    }
}
