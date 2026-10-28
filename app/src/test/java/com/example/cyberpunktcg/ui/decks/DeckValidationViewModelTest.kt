package com.example.cyberpunktcg.ui.decks

import com.example.cyberpunktcg.data.sample.DemoCards
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DeckValidationViewModelTest {
    @Test
    fun warningsFollowWorkingLegendsWithoutRemovingItemsOrBlockingSave() {
        val redLegend = DemoCards.cards.first()
        val extraRed = DemoCards.cards[3].copy(color = redLegend.color)
        val model = DecksViewModel(DemoCards.cards.filterNot { it.id == redLegend.id || it.id == extraRed.id } + redLegend + extraRed)
        val deck = model.createDeck("RAM draft")!!
        model.addLegend(deck.id, "demo1")
        model.addCard(deck.id, "demo5")
        model.addCard(deck.id, "demo5")
        assertTrue(model.validation(deck.id)!!.ramWarnings.isEmpty())
        assertTrue(model.validation(deck.id)!!.colorWarnings.isEmpty())
        assertEquals(2, model.validation(deck.id)!!.mainCount)
        assertTrue(model.validation(deck.id)!!.hasWarnings)
        // Save unfinished drafts
        assertTrue(model.saveDraft(deck.id))
        model.removeLegend(deck.id, "demo1")
        assertEquals(2, model.editingDeck(deck.id)!!.mainCardCount)
        assertFalse(model.validation(deck.id)!!.ramWarnings.isEmpty())
        model.discardChanges(deck.id)
        assertTrue(model.validation(deck.id)!!.colorWarnings.isEmpty())
        assertTrue(model.validation(deck.id)!!.ramWarnings.isEmpty())
        model.addCard(deck.id, "demo4")
        // RAM does not disable additions.
        assertTrue(model.canAddCard(deck.id, "demo4"))
        // low RAM allows deck saving.
        assertTrue(model.saveDraft(deck.id))
        assertFalse(model.validation(deck.id)!!.ramWarnings.isEmpty())
        assertFalse(model.hasUnsavedChanges(deck.id))
        assertNull(model.validation("missing"))
        assertNull(model.validation(null))
    }
}
