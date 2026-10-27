package com.example.cyberpunktcg.ui.browser

import com.example.cyberpunktcg.data.model.CardColor
import com.example.cyberpunktcg.data.model.CardType

internal data class CardBrowserState(
    val query: String = "",
    val selectedTypes: Set<CardType> = emptySet(),
    val selectedColors: Set<CardColor> = emptySet(),
    val sortField: CardSortField = CardSortField.NAME,
    val sortDirection: SortDirection = SortDirection.ASCENDING,
)
