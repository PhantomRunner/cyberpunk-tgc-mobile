package com.example.cyberpunktcg.ui.decks

import com.example.cyberpunktcg.data.model.CardColor
import com.example.cyberpunktcg.data.model.CardType
import com.example.cyberpunktcg.data.model.DeckItem
import com.example.cyberpunktcg.data.sample.DemoCards
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeckColorEligibilityTest {
    @Test
    fun everyMainTypeRequiresAMatchingLegendEvenWhenRamIsZero() {
        for (color in CardColor.entries) {
            val legend = DemoCards.cards[0].copy(color = color, ram = 0)
            val main = CardColor.entries.flatMap { cardColor ->
                listOf(CardType.UNIT, CardType.PROGRAM, CardType.GEAR).map { type ->
                    DemoCards.cards[1].copy(id = "$cardColor-$type", name = "$cardColor $type", color = cardColor, type = type, ram = 0)
                }
            }
            val model = DecksViewModel(listOf(legend) + main)
            val deck = model.createDeck("Colors")!!
            main.forEach { card ->
                assertFalse(model.canAddCard(deck.id, card.id))
                model.addCard(deck.id, card.id)
            }
            assertTrue(model.editingDeck(deck.id)!!.items.isEmpty())
            model.addLegend(deck.id, legend.id)
            main.forEach { card ->
                assertEquals(card.color == color, model.canAddCard(deck.id, card.id))
                model.addCard(deck.id, card.id)
            }
            assertEquals(main.filter { it.color == color }.map { DeckItem(it.id, 1) }, model.editingDeck(deck.id)!!.items)
            assertEquals(setOf(color), model.validation(deck.id)!!.allowedColors)
            assertTrue(model.validation(deck.id)!!.colorWarnings.isEmpty())
        }
    }

    @Test
    fun multipleColorsFormAUnionAndRemovingOneKeepsItemsButBlocksMore() {
        val red = DemoCards.cards[0]
        val blue = red.copy(id = "blue-legend", name = "Blue legend", color = CardColor.BLUE)
        val model = DecksViewModel(DemoCards.cards + blue)
        val deck = model.createDeck("Two colors")!!
        model.addLegend(deck.id, red.id)
        model.addLegend(deck.id, blue.id)
        assertEquals(setOf(CardColor.RED, CardColor.BLUE), model.validation(deck.id)!!.allowedColors)
        assertTrue(model.canAddCard(deck.id, "demo3"))
        assertTrue(model.canAddCard(deck.id, "demo5"))
        assertFalse(model.canAddCard(deck.id, "demo2"))
        model.addCard(deck.id, "demo3")
        model.addCard(deck.id, "demo5")
        model.saveDraft(deck.id)
        model.removeLegend(deck.id, blue.id)
        assertEquals(2, model.editingDeck(deck.id)!!.mainCardCount)
        assertFalse(model.canAddCard(deck.id, "demo3"))
        assertEquals(listOf("demo3"), model.validation(deck.id)!!.colorWarnings.map { it.id })
        model.addCard(deck.id, "demo3")
        assertEquals(1, model.cardQuantity(deck.id, "demo3"))
        model.discardChanges(deck.id)
        assertTrue(model.validation(deck.id)!!.colorWarnings.isEmpty())
        model.removeLegend(deck.id, blue.id)
        model.removeCard(deck.id, "demo3")
        assertEquals(0, model.cardQuantity(deck.id, "demo3"))
        assertTrue(model.validation(deck.id)!!.colorWarnings.isEmpty())
        assertTrue(model.saveDraft(deck.id))
    }
}
