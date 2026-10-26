package com.example.cyberpunktcg.ui.collection

import com.example.cyberpunktcg.data.model.Card

internal enum class CardSortField {
    NAME,
    COST,
    POWER,
    RAM,
    COLOR,
    TYPE,
}

internal enum class SortDirection {
    ASCENDING,
    DESCENDING,
}

internal fun sortCards(
    cards: List<Card>,
    field: CardSortField,
    direction: SortDirection,
): List<Card> {
    val comparator: Comparator<Card> = when (field) {
        CardSortField.NAME -> compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
        CardSortField.COST -> compareBy { it.cost }
        CardSortField.POWER -> compareBy { it.power }
        CardSortField.RAM -> compareBy { it.ram }
        CardSortField.COLOR -> compareBy { it.color.ordinal }
        CardSortField.TYPE -> compareBy { it.type.ordinal }
    }
    val directedComparator = if (direction == SortDirection.DESCENDING) {
        comparator.reversed()
    } else {
        comparator
    }
    // Keep equal values predictable in either direction, including after filtering.
    return cards.sortedWith(
        directedComparator.thenBy(String.CASE_INSENSITIVE_ORDER) { it.name }.thenBy { it.id },
    )
}
