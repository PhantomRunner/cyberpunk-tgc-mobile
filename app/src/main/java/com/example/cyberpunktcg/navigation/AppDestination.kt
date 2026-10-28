package com.example.cyberpunktcg.navigation

import android.net.Uri
import androidx.annotation.StringRes
import com.example.cyberpunktcg.R


internal enum class AppDestination(val route: String, @get:StringRes val titleResId: Int) {
    Home("home", R.string.home),
    Collection("collection", R.string.collection),
    Decks("decks", R.string.my_decks),
    Scanner("scanner", R.string.scanner),
    Settings("settings", R.string.settings),
    CardDetails("card_details/{cardId}", R.string.card_details),
    DeckEditor("deck_editor/{deckId}", R.string.deck_editor),
    DeckCards("deck_cards/{deckId}", R.string.add_cards),
    DeckPreview("deck_preview/{deckId}", R.string.deck_preview),
    DeckCardDetails("deck_card_details/{deckId}/{cardId}", R.string.card_details);


    val mainTab: AppDestination
        get() = when (this) {
            CardDetails -> Collection
            DeckEditor, DeckCards, DeckPreview, DeckCardDetails -> Decks
            else -> this
        }
}

internal fun cardDetailsRoute(cardId: String): String {
    return "card_details/${Uri.encode(cardId)}"
}

internal fun deckEditorRoute(deckId: String): String {
    return "deck_editor/${Uri.encode(deckId)}"
}

internal fun deckCardsRoute(deckId: String): String {
    return "deck_cards/${Uri.encode(deckId)}"
}

internal fun deckPreviewRoute(deckId: String): String {
    return "deck_preview/${Uri.encode(deckId)}"
}

internal fun deckCardDetailsRoute(deckId: String, cardId: String): String {
    return "deck_card_details/${Uri.encode(deckId)}/${Uri.encode(cardId)}"
}
