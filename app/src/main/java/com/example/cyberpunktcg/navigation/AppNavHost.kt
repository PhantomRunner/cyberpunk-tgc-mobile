package com.example.cyberpunktcg.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cyberpunktcg.data.sample.DemoCards
import com.example.cyberpunktcg.ui.card.CardDetailsScreen
import com.example.cyberpunktcg.ui.collection.CollectionScreen
import com.example.cyberpunktcg.ui.decks.DeckEditorScreen
import com.example.cyberpunktcg.ui.decks.DeckPreviewScreen
import com.example.cyberpunktcg.ui.decks.MyDecksScreen
import com.example.cyberpunktcg.ui.home.HomeScreen
import com.example.cyberpunktcg.ui.scanner.ScannerScreen
import com.example.cyberpunktcg.ui.settings.SettingsScreen

@Composable
internal fun AppNavHost(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route,
        modifier = modifier,
    ) {
        composable(AppDestination.Home.route) { HomeScreen() }
        composable(AppDestination.Collection.route) {
            CollectionScreen(
                cards = DemoCards.cards,
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
        composable(AppDestination.Settings.route) {
            SettingsScreen(
                isDarkTheme = isDarkTheme,
                onDarkThemeChange = onDarkThemeChange,
            )
        }
    }
}
