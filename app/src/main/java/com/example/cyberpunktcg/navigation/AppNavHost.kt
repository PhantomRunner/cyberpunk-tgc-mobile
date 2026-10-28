package com.example.cyberpunktcg.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.cyberpunktcg.ui.browser.CardBrowserViewModel
import com.example.cyberpunktcg.ui.decks.DeckCardsScreen
import com.example.cyberpunktcg.ui.decks.DeckEditorScreen
import com.example.cyberpunktcg.ui.decks.DeckPreviewScreen
import com.example.cyberpunktcg.ui.decks.DeckNotFoundScreen
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
    val onBackToDecks: () -> Unit = {
        if (!navController.popBackStack(AppDestination.Decks.route, false)) {
            navController.navigate(AppDestination.Decks.route) { launchSingleTop = true }
        }
    }
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route,
        modifier = modifier,
    ) {
        composable(AppDestination.Home.route) { HomeScreen() }
        composable(AppDestination.Collection.route) {
            val collectionBrowser: CardBrowserViewModel = viewModel()
            CollectionScreen(
                cards = collectionBrowser.visibleCards(DemoCards.cards),
                state = collectionBrowser.uiState,
                onQueryChange = collectionBrowser::setQuery,
                onTypesChange = collectionBrowser::setSelectedTypes,
                onColorsChange = collectionBrowser::setSelectedColors,
                onSortFieldChange = collectionBrowser::setSortField,
                onSortDirectionChange = collectionBrowser::setSortDirection,
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
                validation = decksViewModel.validation(deckId),
                onBack = onEditorBack,
                legends = decksViewModel.legends,
                mainCards = decksViewModel.mainCards,
                onAddCards = { deckId?.let { navController.navigate(deckCardsRoute(it)) { launchSingleTop = true } } },
                onPreview = { deckId?.let { navController.navigate(deckPreviewRoute(it)) { launchSingleTop = true } } },
                hasUnsavedChanges = decksViewModel.hasUnsavedChanges(deckId),
                onNameChange = { name -> deckId?.let { decksViewModel.renameDeck(it, name) } },
                canAddLegend = { cardId -> deckId != null && decksViewModel.canAddLegend(deckId, cardId) },
                onAddLegend = { cardId -> deckId?.let { decksViewModel.addLegend(it, cardId) } },
                onRemoveLegend = { cardId -> deckId?.let { decksViewModel.removeLegend(it, cardId) } },
                onSaveDraft = { deckId?.let { decksViewModel.saveDraft(it) } },
                onBackToDecks = onBackToDecks,
            )
        }
        composable(
            route = AppDestination.DeckCards.route,
            arguments = listOf(navArgument("deckId") { type = NavType.StringType }),
        ) { entry ->
            val deckId = entry.arguments?.getString("deckId")
            val deck = decksViewModel.editingDeck(deckId)


            val browserOwner = remember(entry, deckId) {
                if (deckId != null && decksViewModel.findDeck(deckId) != null) {
                    navController.getBackStackEntry(deckEditorRoute(deckId))
                } else entry
            }
            val browser: CardBrowserViewModel = viewModel(viewModelStoreOwner = browserOwner, key = "deck_cards")
            DeckCardsScreen(
                deck = deck,
                validation = decksViewModel.validation(deckId),
                cards = browser.visibleCards(decksViewModel.mainCards),
                state = browser.uiState,
                onQueryChange = browser::setQuery,
                onTypesChange = browser::setSelectedTypes,
                onColorsChange = browser::setSelectedColors,
                onSortFieldChange = browser::setSortField,
                onSortDirectionChange = browser::setSortDirection,
                canAddCard = { cardId -> deckId != null && decksViewModel.canAddCard(deckId, cardId) },
                onAddCard = { cardId -> deckId?.let { decksViewModel.addCard(it, cardId) } },
                onRemoveCard = { cardId -> deckId?.let { decksViewModel.removeCard(it, cardId) } },
                onBackToDecks = onBackToDecks,
            )
        }
        composable(
            route = AppDestination.DeckPreview.route,
            arguments = listOf(navArgument("deckId") { type = NavType.StringType }),
        ) { entry ->
            val deckId = entry.arguments?.getString("deckId")
            DeckPreviewScreen(
                deck = decksViewModel.editingDeck(deckId),
                legends = decksViewModel.legends,
                mainCards = decksViewModel.mainCards,
                onOpenCard = { cardId ->
                    deckId?.let { navController.navigate(deckCardDetailsRoute(it, cardId)) { launchSingleTop = true } }
                },
                onBackToDecks = onBackToDecks,
            )
        }
        composable(
            route = AppDestination.DeckCardDetails.route,
            arguments = listOf(
                navArgument("deckId") { type = NavType.StringType },
                navArgument("cardId") { type = NavType.StringType },
            ),
        ) { entry ->
            val deck = decksViewModel.editingDeck(entry.arguments?.getString("deckId"))
            if (deck == null) {
                DeckNotFoundScreen(onBackToDecks)
            } else {
                val cardId = entry.arguments?.getString("cardId")
                val card = DemoCards.cards.firstOrNull { card ->
                    card.id == cardId && (card.id in deck.legendIds || deck.items.any { it.cardId == card.id })
                }
                CardDetailsScreen(card)
            }
        }
        composable(AppDestination.Scanner.route) { ScannerScreen() }
        composable(AppDestination.Settings.route) {
            SettingsScreen(
                isDarkTheme = isDarkTheme,
                onDarkThemeChange = onDarkThemeChange,
            )
        }
    }
}
