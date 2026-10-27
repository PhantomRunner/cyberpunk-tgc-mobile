package com.example.cyberpunktcg.ui.collection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.cyberpunktcg.R
import com.example.cyberpunktcg.data.model.Card
import com.example.cyberpunktcg.data.model.CardColor
import com.example.cyberpunktcg.data.model.CardType
import com.example.cyberpunktcg.ui.browser.CardBrowser
import com.example.cyberpunktcg.ui.browser.CardBrowserState
import com.example.cyberpunktcg.ui.browser.CardSortField
import com.example.cyberpunktcg.ui.browser.SortDirection
import com.example.cyberpunktcg.ui.components.CardPreviewTile

@Composable
internal fun CollectionScreen(
    cards: List<Card>,
    state: CardBrowserState,
    onQueryChange: (String) -> Unit,
    onTypesChange: (Set<CardType>) -> Unit,
    onColorsChange: (Set<CardColor>) -> Unit,
    onSortFieldChange: (CardSortField) -> Unit,
    onSortDirectionChange: (SortDirection) -> Unit,
    onOpenCard: (String) -> Unit,
) {
    CardBrowser(
        cards = cards,
        state = state,
        onQueryChange = onQueryChange,
        onTypesChange = onTypesChange,
        onColorsChange = onColorsChange,
        onSortFieldChange = onSortFieldChange,
        onSortDirectionChange = onSortDirectionChange,
        header = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.demo_collection), style = MaterialTheme.typography.titleLarge)
                Text(
                    stringResource(R.string.demo_collection_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        cardContent = { card ->
            CardPreviewTile(card = card, onClick = { onOpenCard(card.id) }, modifier = Modifier.fillMaxWidth())
        },
    )
}
