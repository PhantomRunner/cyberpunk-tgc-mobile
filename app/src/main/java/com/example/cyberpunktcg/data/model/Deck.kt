package com.example.cyberpunktcg.data.model

data class Deck(
    val id: String,
    val name: String,
    val legendIds: List<String> = emptyList(),
    val items: List<DeckItem> = emptyList(),
) {
    val mainCardCount: Int
        get() = items.sumOf { it.quantity }
}
