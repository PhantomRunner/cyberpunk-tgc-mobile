package com.example.cyberpunktcg.ui.decks

import com.example.cyberpunktcg.data.model.DeckItem
import com.example.cyberpunktcg.data.model.CardColor
import com.example.cyberpunktcg.data.sample.DemoCards
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeckMainCardsTest {
    @Test
    fun copyLimitIsSharedAcrossPrintIdsButNotDifferentSubtitles() {
        val card = DemoCards.cards[1]
        val otherPrint = card.copy(id = "another-print")
        val otherSubtitle = card.copy(id = "other-subtitle", subtitle = "Different")
        val legend = DemoCards.cards.first().copy(color = card.color)
        val model = DecksViewModel(listOf(legend, card, otherPrint, otherSubtitle))
        val deck = model.createDeck("Copies")!!
        model.addLegend(deck.id, legend.id)
        model.addCard(deck.id, card.id)
        model.addCard(deck.id, card.id)
        model.addCard(deck.id, otherPrint.id)
        assertFalse(model.canAddCard(deck.id, card.id))
        assertFalse(model.canAddCard(deck.id, otherPrint.id))
        model.addCard(deck.id, otherPrint.id)
        assertEquals(3, model.editingDeck(deck.id)!!.mainCardCount)
        assertTrue(model.canAddCard(deck.id, otherSubtitle.id))
        model.addCard(deck.id, otherSubtitle.id)
        assertEquals(4, model.editingDeck(deck.id)!!.mainCardCount)
        model.removeCard(deck.id, card.id)
        assertTrue(model.canAddCard(deck.id, otherPrint.id))
    }

    @Test
    fun removingLastCopyDeletesEntryAndNeverMakesNegativeQuantities() {
        val legend = DemoCards.cards.first().copy(color = CardColor.YELLOW)
        val model = DecksViewModel(DemoCards.cards.drop(1) + legend)
        val deck = model.createDeck("Remove")!!
        model.addLegend(deck.id, legend.id)
        model.saveDraft(deck.id)
        model.addCard(deck.id, "demo2")
        model.addCard(deck.id, "demo2")
        assertEquals(listOf(DeckItem("demo2", 2)), model.editingDeck(deck.id)!!.items)
        model.removeCard(deck.id, "demo2")
        assertEquals(1, model.cardQuantity(deck.id, "demo2"))
        model.removeCard(deck.id, "demo2")
        model.removeCard(deck.id, "demo2")
        assertEquals(emptyList<DeckItem>(), model.editingDeck(deck.id)!!.items)
        assertEquals(0, model.cardQuantity(deck.id, "demo2"))
        assertFalse(model.hasUnsavedChanges(deck.id))
    }

    @Test
    fun onlySelectedMainColorsCanBeAddedAndDeckIdsRemainIndependent() {
        val model = DecksViewModel()
        val first = model.createDeck("First")!!
        val second = model.createDeck("Second")!!
        assertEquals(listOf("demo2", "demo3", "demo4", "demo5", "demo6"), model.mainCards.map { it.id })
        model.addCard(first.id, "demo1")
        model.addCard(first.id, "unknown")
        model.addCard("missing-deck", "demo2")
        model.removeCard("missing-deck", "demo2")
        assertFalse(model.canAddCard(first.id, "demo1"))
        assertFalse(model.canAddCard(first.id, "unknown"))
        assertFalse(model.canAddCard("missing-deck", "demo2"))
        assertFalse(model.hasUnsavedChanges(first.id))
        model.addLegend(first.id, "demo1")
        model.mainCards.forEach { card -> repeat(4) { model.addCard(first.id, card.id) } }
        assertEquals(3, model.editingDeck(first.id)!!.mainCardCount)
        assertEquals(listOf(DeckItem("demo5", 3)), model.editingDeck(first.id)!!.items)
        assertTrue(model.editingDeck(first.id)!!.items.all { it.quantity == 3 })
        assertEquals(first, model.findDeck(first.id))
        assertEquals(second, model.editingDeck(second.id))
    }

    @Test
    fun saveAndDiscardIncludeItemsAndLegendEditsNeverRemoveThem() {
        val model = DecksViewModel()
        val deck = model.createDeck("Saved")!!
        model.addLegend(deck.id, "demo1")
        model.addCard(deck.id, "demo5")
        model.addCard(deck.id, "demo5")
        assertEquals(0, model.findDeck(deck.id)!!.mainCardCount)
        assertTrue(model.saveDraft(deck.id))
        val saved = model.findDeck(deck.id)!!
        assertEquals(2, saved.mainCardCount)
        model.removeLegend(deck.id, "demo1")
        assertEquals(saved.items, model.editingDeck(deck.id)!!.items)
        model.removeCard(deck.id, "demo5")
        model.addCard(deck.id, "demo5")
        model.discardChanges(deck.id)
        assertEquals(saved, model.editingDeck(deck.id))
        model.removeCard(deck.id, "demo5")
        model.removeCard(deck.id, "demo5")
        assertTrue(model.saveDraft(deck.id))
        assertEquals(emptyList<DeckItem>(), model.findDeck(deck.id)!!.items)
        assertEquals(listOf("demo1"), model.findDeck(deck.id)!!.legendIds)
    }
}
