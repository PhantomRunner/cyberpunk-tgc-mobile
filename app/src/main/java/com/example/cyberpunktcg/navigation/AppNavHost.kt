package com.example.cyberpunktcg.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cyberpunktcg.data.sample.DemoCards
import com.example.cyberpunktcg.ui.card.CardDetailsScreen
import com.example.cyberpunktcg.ui.collection.CollectionScreen
import com.example.cyberpunktcg.ui.collection.CollectionViewModel
import com.example.cyberpunktcg.ui.decks.DeckEditorScreen
import com.example.cyberpunktcg.ui.decks.DeckPreviewScreen
import com.example.cyberpunktcg.ui.decks.DecksViewModel
import com.example.cyberpunktcg.ui.decks.MyDecksScreen
import com.example.cyberpunktcg.ui.home.HomeScreen
import com.example.cyberpunktcg.ui.scanner.ScannerScreen
import com.example.cyberpunktcg.ui.settings.SettingsScreen

@Composable
internal fun AppNavHost(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    decksViewModel: DecksViewModel,
    onEditorBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route,
        modifier = modifier,
    ) {
        composable(AppDestination.Home.route) { HomeScreen() }
        composable(AppDestination.Collection.route) {
            val collectionViewModel: CollectionViewModel = viewModel()
            CollectionScreen(
                cards = collectionViewModel.visibleCards(DemoCards.cards),
                state = collectionViewModel.uiState,
                onQueryChange = collectionViewModel::setQuery,
                onTypesChange = collectionViewModel::setSelectedTypes,
                onColorsChange = collectionViewModel::setSelectedColors,
                onSortFieldChange = collectionViewModel::setSortField,
                onSortDirectionChange = collectionViewModel::setSortDirection,
                onOpenCard = { cardId ->
                    navController.navigate(cardDetailsRoute(cardId)) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(
            route = AppDestination.CardDetails.route,
            arguments = listOf(navArgument("cardId") { type = NavType.StringType }),
        ) { entry ->
            val cardId = entry.arguments?.getString("cardId")
            val card = DemoCards.cards.firstOrNull { it.id == cardId }
            CardDetailsScreen(card = card)
        }
        composable(AppDestination.Decks.route) {
            MyDecksScreen(
                decks = decksViewModel.decks,
                onCreateDeck = { name ->
                    val deck = decksViewModel.createDeck(name)
                    if (deck != null) {
                        navController.navigate(deckEditorRoute(deck.id)) {
                            launchSingleTop = true
                        }
                    }
                },
                onOpenDeck = { deckId ->
                    navController.navigate(deckEditorRoute(deckId)) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(
            route = AppDestination.DeckEditor.route,
            arguments = listOf(navArgument("deckId") { type = NavType.StringType }),
        ) { entry ->
            val deckId = entry.arguments?.getString("deckId")
            val deck = decksViewModel.editingDeck(deckId)
            DeckEditorScreen(
                deck = deck,
                onBack = onEditorBack,
                legends = decksViewModel.legends,
                hasUnsavedChanges = decksViewModel.hasUnsavedChanges(deckId),
                onNameChange = { name -> deckId?.let { decksViewModel.renameDeck(it, name) } },
                canAddLegend = { cardId -> deckId != null && decksViewModel.canAddLegend(deckId, cardId) },
                onAddLegend = { cardId -> deckId?.let { decksViewModel.addLegend(it, cardId) } },
                onRemoveLegend = { cardId -> deckId?.let { decksViewModel.removeLegend(it, cardId) } },
                onSaveDraft = { deckId?.let { decksViewModel.saveDraft(it) } },
                onBackToDecks = {
                    if (!navController.popBackStack(AppDestination.Decks.route, false)) {
                        navController.navigate(AppDestination.Decks.route) {
                            launchSingleTop = true
                        }
                    }
                },
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
