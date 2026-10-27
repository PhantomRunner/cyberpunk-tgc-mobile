package com.example.cyberpunktcg.ui.browser

import androidx.lifecycle.SavedStateHandle
import com.example.cyberpunktcg.data.model.CardColor
import com.example.cyberpunktcg.data.model.CardType
import com.example.cyberpunktcg.data.sample.DemoCards
import org.junit.Assert.assertEquals
import org.junit.Test

class CardBrowserViewModelTest {
    @Test
    fun separateOwnersRetainTheirOwnConditionsAndRestoreThem() {
        val collectionHandle = SavedStateHandle()
        val catalogHandle = SavedStateHandle()
        val collection = CardBrowserViewModel(collectionHandle)
        val catalog = CardBrowserViewModel(catalogHandle)
        collection.setQuery("Test 0")
        catalog.setQuery("  TEST  ")
        catalog.setSelectedTypes(setOf(CardType.UNIT))
        catalog.setSelectedColors(setOf(CardColor.YELLOW))
        catalog.setSortField(CardSortField.COST)
        catalog.setSortDirection(SortDirection.DESCENDING)
        assertEquals(listOf("demo6", "demo2"), catalog.visibleCards(DemoCards.cards).map { it.id })
        assertEquals(listOf("demo1"), collection.visibleCards(DemoCards.cards).map { it.id })
        assertEquals(catalog.uiState, CardBrowserViewModel(catalogHandle).uiState)
        assertEquals(collection.uiState, CardBrowserViewModel(collectionHandle).uiState)
        catalog.setSelectedTypes(emptySet())
        catalog.setSelectedColors(emptySet())
        catalog.setQuery("")
        assertEquals("Test 0", collection.uiState.query)
        assertEquals(6, catalog.visibleCards(DemoCards.cards).size)
    }
}
