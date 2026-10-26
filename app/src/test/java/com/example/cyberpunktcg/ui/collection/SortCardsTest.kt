package com.example.cyberpunktcg.ui.collection

import com.example.cyberpunktcg.data.model.Card
import com.example.cyberpunktcg.data.model.CardColor
import com.example.cyberpunktcg.data.model.CardType
import org.junit.Assert.assertEquals
import org.junit.Test

class SortCardsTest {
    private val cards = listOf(
        card("c", "Zulu", CardType.GEAR, CardColor.GREEN, cost = 3, power = 1, ram = 0),
        card("b", "alpha", CardType.UNIT, CardColor.BLUE, cost = 0, power = 5, ram = 2),
        card("a", "ALPHA", CardType.LEGEND, CardColor.RED, cost = 3, power = 0, ram = 2),
        card("d", "Beta", CardType.PROGRAM, CardColor.YELLOW, cost = 1, power = 0, ram = 4),
    )

    @Test
    fun namesIgnoreCaseAndUseIdForEqualNamesInBothDirections() {
        assertOrder(CardSortField.NAME, SortDirection.ASCENDING, listOf("a", "b", "d", "c"))
        assertOrder(CardSortField.NAME, SortDirection.DESCENDING, listOf("c", "d", "a", "b"))
    }

    @Test
    fun numericFieldsIncludeZeroAndKeepTiesByNameThenId() {
        assertOrder(CardSortField.COST, SortDirection.ASCENDING, listOf("b", "d", "a", "c"))
        assertOrder(CardSortField.COST, SortDirection.DESCENDING, listOf("a", "c", "d", "b"))
        assertOrder(CardSortField.POWER, SortDirection.ASCENDING, listOf("a", "d", "c", "b"))
        assertOrder(CardSortField.POWER, SortDirection.DESCENDING, listOf("b", "c", "a", "d"))
        assertOrder(CardSortField.RAM, SortDirection.ASCENDING, listOf("c", "a", "b", "d"))
        assertOrder(CardSortField.RAM, SortDirection.DESCENDING, listOf("d", "a", "b", "c"))
    }

    @Test
    fun colorsAndTypesFollowEnumDeclarationOrderRatherThanLabels() {
        assertOrder(CardSortField.COLOR, SortDirection.ASCENDING, listOf("a", "d", "b", "c"))
        assertOrder(CardSortField.COLOR, SortDirection.DESCENDING, listOf("c", "b", "d", "a"))
        assertOrder(CardSortField.TYPE, SortDirection.ASCENDING, listOf("a", "b", "d", "c"))
        assertOrder(CardSortField.TYPE, SortDirection.DESCENDING, listOf("c", "d", "b", "a"))
    }

    @Test
    fun sortingAppliesToTheSearchAndMultiselectResultsOnly() {
        val state = CollectionUiState(
            query = "  aLp  ",
            selectedTypes = setOf(CardType.LEGEND, CardType.UNIT),
            selectedColors = setOf(CardColor.RED, CardColor.BLUE),
            sortField = CardSortField.POWER,
            sortDirection = SortDirection.DESCENDING,
        )
        val result = sortCards(filterCards(cards, state), state.sortField, state.sortDirection)
        assertEquals(listOf("b", "a"), result.map { it.id })
    }

    @Test
    fun emptyAndSingleCardListsWorkForEverySortOption() {
        CardSortField.entries.forEach { field ->
            SortDirection.entries.forEach { direction ->
                assertEquals(emptyList<Card>(), sortCards(emptyList(), field, direction))
                assertEquals(listOf(cards.first()), sortCards(listOf(cards.first()), field, direction))
            }
        }
    }

    @Test
    fun sortingDoesNotModifyTheSuppliedCatalogue() {
        val original = cards.toList()
        sortCards(cards, CardSortField.COST, SortDirection.DESCENDING)
        assertEquals(original, cards)
    }

    private fun assertOrder(field: CardSortField, direction: SortDirection, ids: List<String>) {
        assertEquals("$field / $direction", ids, sortCards(cards, field, direction).map { it.id })
    }

    private fun card(
        id: String,
        name: String,
        type: CardType,
        color: CardColor,
        cost: Int,
        power: Int,
        ram: Int,
    ) = Card(
        id = id,
        name = name,
        type = type,
        color = color,
        cost = cost,
        power = power,
        ram = ram,
        subtitle = "",
        unitTypes = emptyList(),
        description = "",
    )
}
