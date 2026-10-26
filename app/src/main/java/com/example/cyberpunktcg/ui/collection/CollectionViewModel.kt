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
            sortField = savedStateHandle.get<String>(SORT_FIELD)
                ?.let { CardSortField.valueOf(it) } ?: CardSortField.NAME,
            sortDirection = savedStateHandle.get<String>(SORT_DIRECTION)
                ?.let { SortDirection.valueOf(it) } ?: SortDirection.ASCENDING,
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

    fun setSortField(field: CardSortField) {
        savedStateHandle[SORT_FIELD] = field.name
        uiState = uiState.copy(sortField = field)
    }

    fun setSortDirection(direction: SortDirection) {
        savedStateHandle[SORT_DIRECTION] = direction.name
        uiState = uiState.copy(sortDirection = direction)
    }

    fun visibleCards(cards: List<Card>): List<Card> = sortCards(
        filterCards(cards, uiState), uiState.sortField, uiState.sortDirection,
    )

    private companion object {
        const val QUERY = "collection_query"
        const val TYPES = "collection_types"
        const val COLORS = "collection_colors"
        const val SORT_FIELD = "collection_sort_field"
        const val SORT_DIRECTION = "collection_sort_direction"
    }
}
