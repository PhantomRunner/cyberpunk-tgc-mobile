package com.example.cyberpunktcg.ui.decks

import com.example.cyberpunktcg.data.model.Deck
import com.example.cyberpunktcg.data.model.DeckItem
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class DecksViewModelTest {
    @Test
    fun blankNamesDoNotCreateOrChangeDecks() {
        val viewModel = DecksViewModel()
        assertNull(viewModel.createDeck(""))
        assertEquals(emptyList<Deck>(), viewModel.decks)
        val deck = viewModel.createDeck("Keep me")!!
        assertNull(viewModel.createDeck(" \n\t "))
        assertEquals(listOf(deck), viewModel.decks)
    }

    @Test
    fun creationTrimsNameAndStartsWithAnEmptyDeckAndUuid() {
        val viewModel = DecksViewModel()
        val deck = viewModel.createDeck("  Neon Crew  ")!!
        assertEquals("Neon Crew", deck.name)
        assertNotNull(UUID.fromString(deck.id))
        assertEquals(emptyList<String>(), deck.legendIds)
        assertEquals(emptyList<DeckItem>(), deck.items)
        assertEquals(0, deck.mainCardCount)
        assertEquals(listOf(deck), viewModel.decks)
    }

    @Test
    fun duplicateNamesRemainSeparateAndLookupUsesIds() {
        val viewModel = DecksViewModel()
        val first = viewModel.createDeck("Same name")!!
        val second = viewModel.createDeck("Same name")!!
        assertNotEquals(first.id, second.id)
        assertEquals(listOf(first, second), viewModel.decks)
        assertEquals(first, viewModel.findDeck(first.id))
        assertEquals(second, viewModel.findDeck(second.id))
        assertNull(viewModel.findDeck("missing"))
        assertNull(viewModel.findDeck(null))
    }

    @Test
    fun mainCountCountsCopiesAndExcludesLegends() {
        val deck = Deck(
            id = "deck1",
            name = "Counting example",
            legendIds = listOf("legend1", "legend2"),
            items = listOf(DeckItem("card1", 2), DeckItem("card2", 3)),
        )
        assertEquals(5, deck.mainCardCount)
        assertEquals(2, deck.legendIds.size)
    }
}
