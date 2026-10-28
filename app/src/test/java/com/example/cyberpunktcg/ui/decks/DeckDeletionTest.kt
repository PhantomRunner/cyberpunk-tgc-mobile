package com.example.cyberpunktcg.ui.decks

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DeckDeletionTest {
    @Test
    fun deletingByIdRemovesSavedAndWorkingCopiesButKeepsSameNameDeck() {
        val model = DecksViewModel()
        val first = model.createDeck("Same name")!!
        val second = model.createDeck("Same name")!!
        model.addLegend(first.id, "demo1")
        model.addCard(first.id, "demo5")
        model.saveDraft(first.id)
        model.renameDeck(first.id, "Unsaved first")
        model.addCard(first.id, "demo5")
        model.renameDeck(second.id, "Keep second draft")
        model.addLegend(second.id, "demo1")
        model.addCard(second.id, "demo5")
        val secondDraft = model.editingDeck(second.id)

        model.deleteDeck(first.id)

        assertEquals(listOf(second), model.decks)
        assertNull(model.findDeck(first.id))
        assertNull(model.editingDeck(first.id))
        assertNull(model.validation(first.id))
        assertFalse(model.hasUnsavedChanges(first.id))
        assertEquals(secondDraft, model.editingDeck(second.id))
        assertTrue(model.hasUnsavedChanges(second.id))
        // A stale editor callback cannot resurrect the deleted draft.
        model.renameDeck(first.id, "Resurrect")
        model.addLegend(first.id, "demo1")
        model.addCard(first.id, "demo5")
        assertFalse(model.saveDraft(first.id))
        assertNull(model.editingDeck(first.id))
        assertTrue(model.saveDraft(second.id))
        assertEquals(secondDraft, model.findDeck(second.id))
    }

    @Test
    fun missingAndRepeatedDeletesAreSafeAndLastDeleteAllowsFreshCreation() {
        val model = DecksViewModel()
        model.deleteDeck("missing")
        assertTrue(model.decks.isEmpty())
        val deck = model.createDeck("Last")!!
        model.addLegend(deck.id, "demo1")
        model.addCard(deck.id, "demo5")
        val draft = model.editingDeck(deck.id)
        model.deleteDeck("missing")
        assertEquals(listOf(deck), model.decks)
        assertEquals(draft, model.editingDeck(deck.id))
        model.deleteDeck(deck.id)
        model.deleteDeck(deck.id)
        assertTrue(model.decks.isEmpty())
        assertNull(model.editingDeck(deck.id))
        val fresh = model.createDeck("Last")!!
        assertNotEquals(deck.id, fresh.id)
        assertEquals(listOf(fresh), model.decks)
        assertTrue(fresh.items.isEmpty())
        assertTrue(fresh.legendIds.isEmpty())
        assertFalse(model.hasUnsavedChanges(fresh.id))
    }
}
