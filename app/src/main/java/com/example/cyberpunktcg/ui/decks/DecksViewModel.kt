package com.example.cyberpunktcg.ui.decks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.cyberpunktcg.data.model.Deck
import java.util.UUID

internal class DecksViewModel : ViewModel() {
    var decks: List<Deck> by mutableStateOf(emptyList())
        private set

    fun createDeck(name: String): Deck? {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return null
        }

        val deck = Deck(id = UUID.randomUUID().toString(), name = trimmedName)
        decks = decks + deck
        return deck
    }

    fun findDeck(deckId: String?): Deck? {
        return decks.firstOrNull { it.id == deckId }
    }
}
