package com.example.cyberpunktcg.ui.decks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.cyberpunktcg.data.model.Card
import com.example.cyberpunktcg.data.model.CardType
import com.example.cyberpunktcg.data.model.Deck
import com.example.cyberpunktcg.data.model.DeckItem
import com.example.cyberpunktcg.data.sample.DemoCards
import java.util.UUID
import com.example.cyberpunktcg.domain.DeckValidation
import com.example.cyberpunktcg.domain.countCardCopies
import com.example.cyberpunktcg.domain.validateDeck

internal class DecksViewModel(private val cards: List<Card> = DemoCards.cards) : ViewModel() {
    var decks: List<Deck> by mutableStateOf(emptyList())
        private set

    // deck ID as a key to keep suspended editor intact with the deck's edits.
    private var workingCopies: Map<String, Deck> by mutableStateOf(emptyMap())

    val legends: List<Card> = cards.filter { it.type == CardType.LEGEND }
    val mainCards: List<Card> = cards.filter { it.type != CardType.LEGEND }

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

    fun editingDeck(deckId: String?): Deck? {
        return workingCopies[deckId] ?: findDeck(deckId)
    }

    fun hasUnsavedChanges(deckId: String?): Boolean {
        return editingDeck(deckId) != findDeck(deckId)
    }

    fun renameDeck(deckId: String, name: String) {
        val deck = editingDeck(deckId) ?: return
        workingCopies = workingCopies + (deckId to deck.copy(name = name))
    }

    fun canAddLegend(deckId: String, cardId: String): Boolean {
        val deck = editingDeck(deckId) ?: return false
        val legend = legends.firstOrNull { it.id == cardId } ?: return false
        val selectedNames = legends.filter { it.id in deck.legendIds }.map { it.name }
        return deck.legendIds.size < 3 && legend.name !in selectedNames
    }

    fun addLegend(deckId: String, cardId: String) {
        if (!canAddLegend(deckId, cardId)) return
        val deck = editingDeck(deckId) ?: return
        workingCopies = workingCopies + (deckId to deck.copy(legendIds = deck.legendIds + cardId))
    }

    fun removeLegend(deckId: String, cardId: String) {
        val deck = editingDeck(deckId) ?: return
        workingCopies = workingCopies + (deckId to deck.copy(legendIds = deck.legendIds - cardId))
    }

    fun cardQuantity(deckId: String, cardId: String): Int {
        return editingDeck(deckId)?.items?.firstOrNull { it.cardId == cardId }?.quantity ?: 0
    }

    fun canAddCard(deckId: String, cardId: String): Boolean {
        val deck = editingDeck(deckId) ?: return false
        val card = mainCards.firstOrNull { it.id == cardId } ?: return false
        return countCardCopies(deck.items, cards, card) < 3
    }

    fun addCard(deckId: String, cardId: String) {
        if (!canAddCard(deckId, cardId)) return
        val deck = editingDeck(deckId) ?: return
        val quantity = cardQuantity(deckId, cardId)
        val items = if (quantity == 0) {
            deck.items + DeckItem(cardId, 1)
        } else {
            deck.items.map { if (it.cardId == cardId) it.copy(quantity = it.quantity + 1) else it }
        }
        workingCopies = workingCopies + (deckId to deck.copy(items = items))
    }

    fun removeCard(deckId: String, cardId: String) {
        val deck = editingDeck(deckId) ?: return
        val items = deck.items.mapNotNull { item ->
            when {
                item.cardId != cardId -> item
                item.quantity > 1 -> item.copy(quantity = item.quantity - 1)
                else -> null
            }
        }
        workingCopies = workingCopies + (deckId to deck.copy(items = items))
    }

    fun validation(deckId: String?): DeckValidation? {
        val deck = editingDeck(deckId) ?: return null
        return validateDeck(deck, cards)
    }

    fun saveDraft(deckId: String): Boolean {
        val deck = editingDeck(deckId) ?: return false
        val name = deck.name.trim()
        if (name.isEmpty()) return false
        decks = decks.map { if (it.id == deckId) deck.copy(name = name) else it }
        workingCopies = workingCopies - deckId
        return true
    }

    fun discardChanges(deckId: String) {
        workingCopies = workingCopies - deckId
    }
}
