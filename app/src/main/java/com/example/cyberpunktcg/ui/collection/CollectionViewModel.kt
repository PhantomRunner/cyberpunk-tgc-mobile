package com.example.cyberpunktcg.ui.collection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.cyberpunktcg.data.model.Card
import com.example.cyberpunktcg.data.model.CardColor
import com.example.cyberpunktcg.data.model.CardType

internal class CollectionViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    var uiState by mutableStateOf(
        CollectionUiState(
            query = savedStateHandle[QUERY] ?: "",
            selectedTypes = savedStateHandle.get<ArrayList<String>>(TYPES)
                ?.map { CardType.valueOf(it) }?.toSet() ?: emptySet(),
            selectedColors = savedStateHandle.get<ArrayList<String>>(COLORS)
                ?.map { CardColor.valueOf(it) }?.toSet() ?: emptySet(),
        ),
    )
        private set

    fun setQuery(query: String) {
        savedStateHandle[QUERY] = query
        uiState = uiState.copy(query = query)
    }

    fun setSelectedTypes(types: Set<CardType>) {
        // Names remain meaningful if enum entries are reordered; ordinals do not.
        savedStateHandle[TYPES] = ArrayList(types.map { it.name })
        uiState = uiState.copy(selectedTypes = types)
    }

    fun setSelectedColors(colors: Set<CardColor>) {
        savedStateHandle[COLORS] = ArrayList(colors.map { it.name })
        uiState = uiState.copy(selectedColors = colors)
    }

    fun matchingCards(cards: List<Card>): List<Card> = filterCards(cards, uiState)

    private companion object {
        const val QUERY = "collection_query"
        const val TYPES = "collection_types"
        const val COLORS = "collection_colors"
    }
}
