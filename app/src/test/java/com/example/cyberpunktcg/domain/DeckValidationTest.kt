package com.example.cyberpunktcg.domain

import com.example.cyberpunktcg.data.model.CardColor
import com.example.cyberpunktcg.data.model.CardType
import com.example.cyberpunktcg.data.model.Deck
import com.example.cyberpunktcg.data.model.DeckItem
import com.example.cyberpunktcg.data.sample.DemoCards
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeckValidationTest {
    private val legend = DemoCards.cards[0]
    private val unit = DemoCards.cards[1]

    @Test
    fun emptyDeckHasFourZeroLimitsAndIncompleteCounts() {
        val result = validateDeck(Deck("empty", "Empty"), DemoCards.cards)
        assertEquals(CardColor.entries.associateWith { 0 }, result.ramLimits)
        assertEquals(0, result.mainCount)
        assertEquals(0, result.legendCount)
        assertTrue(result.hasMainCountWarning)
        assertTrue(result.hasLegendCountWarning)
        assertTrue(result.hasWarnings)
        assertTrue(result.ramWarnings.isEmpty())
    }

    @Test
    fun mainCountUsesCopiesAndInclusiveFortyFiftyBoundsWithoutLegends() {
        for (quantity in listOf(39, 40, 50, 51)) {
            val deck = Deck("bounds", "Bounds", items = listOf(DeckItem(unit.id, quantity), DeckItem(legend.id, 1)))
            val result = validateDeck(deck, listOf(legend, unit))
            assertEquals(quantity, result.mainCount)
            assertEquals(quantity == 39 || quantity == 51, result.hasMainCountWarning)
            assertEquals(listOf(legend), result.legendsInMain)
        }
        val split = Deck("sum", "Sum", items = listOf(DeckItem(unit.id, 2), DeckItem("second", 3)))
        assertEquals(5, validateDeck(split, listOf(unit, unit.copy(id = "second"))).mainCount)
    }

    @Test
    fun exactlyThreeLegendsAndTheirNamesAreCheckedSeparately() {
        val secondPrint = legend.copy(id = "second", subtitle = "Different subtitle")
        val third = legend.copy(id = "third", name = "Another name")
        val fourth = legend.copy(id = "fourth", name = "Fourth")
        val cards = listOf(legend, secondPrint, third, fourth)
        for (count in 0..4) {
            val deck = Deck("legends", "Legends", legendIds = cards.take(count).map { it.id })
            assertEquals(count != 3, validateDeck(deck, cards).hasLegendCountWarning)
        }
        val deck = Deck("duplicate", "Duplicate", legendIds = listOf(legend.id, secondPrint.id, third.id))
        val result = validateDeck(deck, cards)
        assertFalse(result.hasLegendCountWarning)
        assertEquals(listOf(legend.name), result.duplicateLegendNames)
    }

    @Test
    fun copyLimitGroupsNameAndSubtitleAcrossDifferentPrintIds() {
        val print = unit.copy(id = "other-print")
        val otherSubtitle = unit.copy(id = "other-title", subtitle = "Other")
        val deck = Deck("copies", "Copies", items = listOf(
            DeckItem(unit.id, 2), DeckItem(print.id, 2), DeckItem(otherSubtitle.id, 3),
        ))
        val result = validateDeck(deck, listOf(unit, print, otherSubtitle))
        assertEquals(listOf(CardCopyWarning(unit.name, unit.subtitle, 4)), result.copyWarnings)
        val atLimit = deck.copy(items = listOf(DeckItem(unit.id, 2), DeckItem(print.id, 1)))
        assertTrue(validateDeck(atLimit, listOf(unit, print)).copyWarnings.isEmpty())
    }

    @Test
    fun ramLimitsSumOnlyLegendsOfEachColor() {
        val legends = listOf(
            legend,
            legend.copy(id = "red2", name = "Red two", ram = 3),
            legend.copy(id = "blue", name = "Blue", color = CardColor.BLUE, ram = 4),
        )
        val limits = calculateRamLimits(legends + unit.copy(color = CardColor.RED, ram = 999))
        assertEquals(mapOf(CardColor.RED to 5, CardColor.YELLOW to 0, CardColor.BLUE to 4, CardColor.GREEN to 0), limits)
    }

    @Test
    fun ramZeroNeedsNoBudgetButStillRequiresLegendColor() {
        val deck = Deck("zero", "Zero", items = DemoCards.cards.drop(1).map { DeckItem(it.id, 1) })
        val result = validateDeck(deck, DemoCards.cards)
        assertEquals(listOf("demo4", "demo5", "demo6"), result.ramWarnings.map { it.cardId })
        assertTrue(result.ramWarnings.all { it.availableRam == 0 })
        assertFalse(result.ramWarnings.any { it.cardId == "demo2" || it.cardId == "demo3" })
        assertEquals(DemoCards.cards.drop(1).map { it.id }, result.colorWarnings.map { it.id })
    }

    @Test
    fun ramIsAnInclusivePerCardThresholdAndCopiesDoNotSpendIt() {
        val redLegend = legend.copy(ram = 2)
        val redOne = unit.copy(id = "red1", name = "Red one", color = CardColor.RED, ram = 2)
        val redTwo = redOne.copy(id = "red2", name = "Red two")
        val redTooHigh = redOne.copy(id = "red3", name = "Red too high", ram = 3)
        val yellow = redOne.copy(id = "yellow", name = "Yellow", color = CardColor.YELLOW, ram = 1)
        val deck = Deck("ram", "RAM", legendIds = listOf(redLegend.id), items = listOf(
            DeckItem(redOne.id, 3), DeckItem(redTwo.id, 3), DeckItem(redTooHigh.id, 1), DeckItem(yellow.id, 1),
        ))
        val result = validateDeck(deck, listOf(redLegend, redOne, redTwo, redTooHigh, yellow))
        assertEquals(listOf(redTooHigh.id, yellow.id), result.ramWarnings.map { it.cardId })
        assertEquals(listOf(2, 0), result.ramWarnings.map { it.availableRam })
    }

    @Test
    fun syntheticCompleteCompositionHasNoBasicWarnings() {
        val legends = (1..3).map { legend.copy(id = "legend$it", name = "Legend $it", color = unit.color) }
        val main = (1..14).map { unit.copy(id = "main$it", name = "Main $it", ram = 0) }
        val deck = Deck("complete", "Complete", legendIds = legends.map { it.id },
            items = main.mapIndexed { index, card -> DeckItem(card.id, if (index == 13) 1 else 3) })
        val result = validateDeck(deck, legends + main)
        assertEquals(40, result.mainCount)
        assertFalse(result.hasWarnings)
    }
    @Test
    fun completeRamZeroDeckStillWarnsWhenLegendColorsDoNotMatch() {
        val legends = (1..3).map { legend.copy(id = "legend$it", name = "Legend $it") }
        val main = (1..14).map { unit.copy(id = "main$it", name = "Main $it", ram = 0) }
        val deck = Deck("colors", "Colors", legendIds = legends.map { it.id },
            items = main.mapIndexed { index, card -> DeckItem(card.id, if (index == 13) 1 else 3) })
        val result = validateDeck(deck, legends + main)
        assertFalse(result.hasMainCountWarning)
        assertFalse(result.hasLegendCountWarning)
        assertTrue(result.copyWarnings.isEmpty())
        assertTrue(result.ramWarnings.isEmpty())
        assertEquals(main.map { it.id }, result.colorWarnings.map { it.id })
        assertTrue(result.hasWarnings)
    }

}
