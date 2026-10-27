package com.example.cyberpunktcg.ui.decks

import com.example.cyberpunktcg.data.model.CardType
import com.example.cyberpunktcg.data.sample.DemoCards
import com.example.cyberpunktcg.data.model.Deck
import com.example.cyberpunktcg.data.model.DeckItem
import java.util.UUID
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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

    @Test
    fun editsStaySeparateUntilSaveAndDiscardRestoresLastSave() {
        val viewModel = DecksViewModel()
        val deck = viewModel.createDeck("Original")!!
        viewModel.renameDeck(deck.id, "  Renamed  ")
        viewModel.addLegend(deck.id, "demo1")
        assertEquals(deck, viewModel.findDeck(deck.id))
        assertTrue(viewModel.hasUnsavedChanges(deck.id))
        assertEquals(listOf("demo1"), viewModel.editingDeck(deck.id)!!.legendIds)

        assertTrue(viewModel.saveDraft(deck.id))
        val saved = viewModel.findDeck(deck.id)!!
        assertEquals("Renamed", saved.name)
        assertEquals(listOf("demo1"), saved.legendIds)
        assertFalse(viewModel.hasUnsavedChanges(deck.id))

        viewModel.renameDeck(deck.id, "Discard this")
        viewModel.removeLegend(deck.id, "demo1")
        viewModel.discardChanges(deck.id)
        assertEquals(saved, viewModel.editingDeck(deck.id))
        assertFalse(viewModel.hasUnsavedChanges(deck.id))
    }

    @Test
    fun blankSaveIsRejectedAndRevertingChangesIsClean() {
        val viewModel = DecksViewModel()
        val deck = viewModel.createDeck("Keep")!!
        viewModel.renameDeck(deck.id, "  ")
        assertFalse(viewModel.saveDraft(deck.id))
        assertEquals(deck, viewModel.findDeck(deck.id))
        assertTrue(viewModel.hasUnsavedChanges(deck.id))
        viewModel.renameDeck(deck.id, "Keep")
        viewModel.addLegend(deck.id, "demo1")
        viewModel.removeLegend(deck.id, "demo1")
        assertFalse(viewModel.hasUnsavedChanges(deck.id))
        // empty deck remains as Draft.
        assertTrue(viewModel.saveDraft(deck.id))
    }

    @Test
    fun legendsRequireDistinctNamesAndNeverExceedThree() {
        val first = DemoCards.cards.first()
        val sameName = first.copy(id = "other-print", subtitle = "Different subtitle")
        val second = first.copy(id = "second", name = "Second")
        val third = first.copy(id = "third", name = "Third")
        val fourth = first.copy(id = "fourth", name = "Fourth")
        val unit = first.copy(id = "unit", type = CardType.UNIT)
        val viewModel = DecksViewModel(listOf(first, sameName, second, third, fourth, unit))
        val deck = viewModel.createDeck("Limits")!!
        viewModel.addLegend(deck.id, first.id)
        assertFalse(viewModel.canAddLegend(deck.id, first.id))
        assertFalse(viewModel.canAddLegend(deck.id, sameName.id))
        viewModel.addLegend(deck.id, sameName.id)
        viewModel.addLegend(deck.id, unit.id)
        viewModel.addLegend(deck.id, "unknown")
        assertEquals(listOf(first.id), viewModel.editingDeck(deck.id)!!.legendIds)
        viewModel.addLegend(deck.id, second.id)
        viewModel.addLegend(deck.id, third.id)
        viewModel.addLegend(deck.id, fourth.id)
        assertEquals(listOf(first.id, second.id, third.id), viewModel.editingDeck(deck.id)!!.legendIds)
        assertFalse(viewModel.canAddLegend(deck.id, fourth.id))
        viewModel.removeLegend(deck.id, first.id)
        assertTrue(viewModel.canAddLegend(deck.id, sameName.id))
        viewModel.addLegend(deck.id, sameName.id)
        assertEquals(listOf(second.id, third.id, sameName.id), viewModel.editingDeck(deck.id)!!.legendIds)
    }

    @Test
    fun draftsAreIsolatedByIdAndMissingIdsCannotCreateDecks() {
        val viewModel = DecksViewModel()
        val first = viewModel.createDeck("First")!!
        val second = viewModel.createDeck("Second")!!
        viewModel.renameDeck(first.id, "First draft")
        viewModel.addLegend(first.id, "demo1")
        viewModel.renameDeck(second.id, "Second draft")
        viewModel.saveDraft(second.id)
        assertEquals("First draft", viewModel.editingDeck(first.id)!!.name)
        assertEquals(first, viewModel.findDeck(first.id))
        assertTrue(viewModel.hasUnsavedChanges(first.id))
        assertEquals("Second draft", viewModel.findDeck(second.id)!!.name)
        assertEquals(emptyList<String>(), viewModel.editingDeck(second.id)!!.legendIds)

        viewModel.renameDeck("missing", "Invalid")
        viewModel.addLegend("missing", "demo1")
        viewModel.removeLegend("missing", "demo1")
        viewModel.discardChanges("missing")
        assertFalse(viewModel.saveDraft("missing"))
        assertFalse(viewModel.canAddLegend("missing", "demo1"))
        assertFalse(viewModel.hasUnsavedChanges("missing"))
        assertNull(viewModel.editingDeck(null))
        assertEquals(2, viewModel.decks.size)
        assertTrue(DecksViewModel().decks.isEmpty())
    }
}
