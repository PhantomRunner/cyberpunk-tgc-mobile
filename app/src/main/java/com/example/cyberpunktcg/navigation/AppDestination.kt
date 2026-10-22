package com.example.cyberpunktcg.navigation

import androidx.annotation.StringRes
import com.example.cyberpunktcg.R

// A route is the unique name NavHost uses to find a screen.
internal enum class AppDestination(val route: String, @get:StringRes val titleResId: Int) {
    Home("home", R.string.home),
    Collection("collection", R.string.collection),
    Decks("decks", R.string.my_decks),
    Scanner("scanner", R.string.scanner),
    Settings("settings", R.string.settings),
    CardDetails("card_details", R.string.card_details),
    DeckEditor("deck_editor", R.string.deck_editor),
    DeckPreview("deck_preview", R.string.deck_preview);

    // Detail screens keep their parent tab selected in the bottom bar.
    val mainTab: AppDestination
        get() = when (this) {
            CardDetails -> Collection
            DeckEditor, DeckPreview -> Decks
            else -> this
        }
}
