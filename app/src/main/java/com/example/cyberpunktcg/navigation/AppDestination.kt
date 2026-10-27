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
    DeckPreview("deck_preview", R.string.deck_preview);


    val mainTab: AppDestination
        get() = when (this) {
            CardDetails -> Collection
            DeckEditor, DeckPreview -> Decks
            else -> this
        }
}

internal fun cardDetailsRoute(cardId: String): String {
    return "card_details/${Uri.encode(cardId)}"
}

internal fun deckEditorRoute(deckId: String): String {
    return "deck_editor/${Uri.encode(deckId)}"
}
