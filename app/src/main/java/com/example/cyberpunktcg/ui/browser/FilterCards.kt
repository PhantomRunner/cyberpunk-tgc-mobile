package com.example.cyberpunktcg.ui.browser

import com.example.cyberpunktcg.data.model.Card

internal fun filterCards(cards: List<Card>, state: CardBrowserState): List<Card> {
    val query = state.query.trim()
    return cards.filter { card ->
        val matchesName = card.name.contains(query, ignoreCase = true)
        val matchesType = state.selectedTypes.isEmpty() || card.type in state.selectedTypes
        val matchesColor = state.selectedColors.isEmpty() || card.color in state.selectedColors
        matchesName && matchesType && matchesColor
    }
}
