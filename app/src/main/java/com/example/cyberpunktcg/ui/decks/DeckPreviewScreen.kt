package com.example.cyberpunktcg.ui.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.cyberpunktcg.R
import com.example.cyberpunktcg.data.model.Card
import com.example.cyberpunktcg.data.model.Deck
import com.example.cyberpunktcg.ui.components.CardPreviewTile

@Composable
internal fun DeckPreviewScreen(
    deck: Deck?,
    legends: List<Card>,
    mainCards: List<Card>,
    onOpenCard: (String) -> Unit,
    onBackToDecks: () -> Unit,
) {
    if (deck == null) {
        DeckNotFoundScreen(onBackToDecks)
        return
    }
    val selectedLegends = deck.legendIds.mapNotNull { id -> legends.firstOrNull { it.id == id } }
    val selectedMain = deck.items.mapNotNull { item ->
        mainCards.firstOrNull { it.id == item.cardId }?.let { card -> card to item.quantity }
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        state = rememberLazyGridState(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "preview_heading", span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    if (deck.name.isBlank()) stringResource(R.string.unnamed_draft) else deck.name,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(stringResource(R.string.deck_draft), style = MaterialTheme.typography.labelLarge)
                Text(stringResource(R.string.deck_preview_notice))
            }
        }
        item(key = "legends_heading", span = { GridItemSpan(maxLineSpan) }) {
            Text(stringResource(R.string.deck_legend_count, deck.legendIds.size), style = MaterialTheme.typography.titleMedium)
        }
        if (selectedLegends.isEmpty()) {
            item(key = "no_legends", span = { GridItemSpan(maxLineSpan) }) {
                Text(stringResource(R.string.no_legends_selected))
            }
        }
        items(selectedLegends, key = { "legend_${it.id}" }) { card ->
            CardPreviewTile(card, onClick = { onOpenCard(card.id) }, modifier = Modifier.fillMaxWidth())
        }
        item(key = "main_heading", span = { GridItemSpan(maxLineSpan) }) {
            Text(stringResource(R.string.deck_main_count, deck.mainCardCount), style = MaterialTheme.typography.titleMedium)
        }
        if (selectedMain.isEmpty()) {
            item(key = "no_main", span = { GridItemSpan(maxLineSpan) }) {
                Text(stringResource(R.string.no_main_cards))
            }
        }
        items(selectedMain, key = { "main_${it.first.id}" }) { (card, quantity) ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CardPreviewTile(card, onClick = { onOpenCard(card.id) }, modifier = Modifier.fillMaxWidth())
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.small) {
                    Text(stringResource(R.string.deck_card_copies, quantity), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }
    }
}
