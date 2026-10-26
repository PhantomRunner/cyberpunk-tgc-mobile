package com.example.cyberpunktcg.ui.collection

import com.example.cyberpunktcg.data.model.Card
import com.example.cyberpunktcg.data.model.CardColor
import com.example.cyberpunktcg.data.model.CardType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterCardsTest {
    private val cards = listOf(
        card("1", "Neon Leader", CardType.LEGEND, CardColor.RED),
        card("2", "Neon Runner", CardType.UNIT, CardColor.RED),
        card("3", "Blue Runner", CardType.UNIT, CardColor.BLUE),
        card("4", "Neon Tool", CardType.GEAR, CardColor.YELLOW),
        card("5", "Green Program", CardType.PROGRAM, CardColor.GREEN),
    )

    @Test
    fun emptyGroupsAndBlankQueryKeepAllCardsInOriginalOrder() {
        assertEquals(cards, filterCards(cards, CollectionUiState(query = "   ")))
    }

    @Test
    fun multipleTypesIncludeAnySelectedTypeWithoutRestrictingColors() {
        val state = CollectionUiState(selectedTypes = setOf(CardType.LEGEND, CardType.UNIT))
        assertEquals(listOf("1", "2", "3"), filterCards(cards, state).map { it.id })
    }

    @Test
    fun multipleColorsIncludeAnySelectedColorWithoutRestrictingTypes() {
        val state = CollectionUiState(selectedColors = setOf(CardColor.RED, CardColor.BLUE))
        assertEquals(listOf("1", "2", "3"), filterCards(cards, state).map { it.id })
    }

    @Test
    fun legendAndUnitWithRedIncludeBothRedTypesOnly() {
        val state = CollectionUiState(
            selectedTypes = setOf(CardType.LEGEND, CardType.UNIT),
            selectedColors = setOf(CardColor.RED),
        )
        assertEquals(listOf("1", "2"), filterCards(cards, state).map { it.id })
    }

    @Test
    fun queryIsTrimmedCaseInsensitiveAndCombinedWithBothGroups() {
        val state = CollectionUiState(
            query = "  rUnN  ",
            selectedTypes = setOf(CardType.LEGEND, CardType.UNIT),
            selectedColors = setOf(CardColor.RED, CardColor.YELLOW),
        )
        assertEquals(listOf("2"), filterCards(cards, state).map { it.id })
    }

    @Test
    fun incompatibleGroupsOrQueryGiveEmptyResults() {
        val state = CollectionUiState(
            selectedTypes = setOf(CardType.PROGRAM),
            selectedColors = setOf(CardColor.RED),
        )
        assertTrue(filterCards(cards, state).isEmpty())
        assertTrue(filterCards(cards, CollectionUiState(query = "missing")).isEmpty())
        assertTrue(filterCards(emptyList(), CollectionUiState()).isEmpty())
    }

    @Test
    fun clearingOneConditionKeepsTheOtherRestrictions() {
        val state = CollectionUiState(
            query = "Neon",
            selectedTypes = setOf(CardType.UNIT),
            selectedColors = setOf(CardColor.RED),
        )
        assertEquals(listOf("2"), filterCards(cards, state.copy(query = "")).map { it.id })
        assertEquals(
            listOf("1", "2"),
            filterCards(cards, state.copy(selectedTypes = emptySet())).map { it.id },
        )
        assertEquals(
            listOf("2"),
            filterCards(cards, state.copy(selectedColors = emptySet())).map { it.id },
        )
    }

    private fun card(id: String, name: String, type: CardType, color: CardColor) = Card(
        id = id,
        name = name,
        type = type,
        color = color,
        cost = 0,
        power = 0,
        ram = 0,
        subtitle = "",
        unitTypes = emptyList(),
        description = "",
    )
}
