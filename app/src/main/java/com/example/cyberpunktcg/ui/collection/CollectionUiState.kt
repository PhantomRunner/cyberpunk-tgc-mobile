package com.example.cyberpunktcg.ui.collection

import com.example.cyberpunktcg.data.model.CardColor
import com.example.cyberpunktcg.data.model.CardType

internal data class CollectionUiState(
    val query: String = "",
    val selectedTypes: Set<CardType> = emptySet(),
    val selectedColors: Set<CardColor> = emptySet(),
)
