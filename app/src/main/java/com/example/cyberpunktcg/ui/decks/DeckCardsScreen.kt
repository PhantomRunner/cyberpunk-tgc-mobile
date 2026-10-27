package com.example.cyberpunktcg.ui.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.cyberpunktcg.R
import com.example.cyberpunktcg.data.model.Card
import com.example.cyberpunktcg.data.model.CardColor
import com.example.cyberpunktcg.data.model.CardType
import com.example.cyberpunktcg.data.model.Deck
import com.example.cyberpunktcg.domain.DeckValidation
import com.example.cyberpunktcg.ui.browser.CardBrowser
import com.example.cyberpunktcg.ui.browser.CardBrowserState
import com.example.cyberpunktcg.ui.browser.CardSortField
import com.example.cyberpunktcg.ui.browser.SortDirection
import com.example.cyberpunktcg.ui.components.CardPreviewTile

@Composable
internal fun DeckCardsScreen(
    deck: Deck?,
    validation: DeckValidation?,
    cards: List<Card>,
    state: CardBrowserState,
    onQueryChange: (String) -> Unit,
    onTypesChange: (Set<CardType>) -> Unit,
    onColorsChange: (Set<CardColor>) -> Unit,
    onSortFieldChange: (CardSortField) -> Unit,
    onSortDirectionChange: (SortDirection) -> Unit,
    canAddCard: (String) -> Boolean,
    onAddCard: (String) -> Unit,
    onRemoveCard: (String) -> Unit,
    onBackToDecks: () -> Unit,
) {
    if (deck == null) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.deck_not_found))
            Button(onClick = onBackToDecks) { Text(stringResource(R.string.back_to_my_decks)) }
        }
        return
    }
    CardBrowser(
        cards = cards,
        state = state,
        onQueryChange = onQueryChange,
        onTypesChange = onTypesChange,
        onColorsChange = onColorsChange,
        onSortFieldChange = onSortFieldChange,
        onSortDirectionChange = onSortDirectionChange,
        availableTypes = CardType.entries.filter { it != CardType.LEGEND },
        header = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(deck.name, style = MaterialTheme.typography.titleLarge)
                Text(stringResource(R.string.deck_main_count, deck.mainCardCount))
                Text(stringResource(R.string.deck_cards_hint), style = MaterialTheme.typography.bodyMedium)
            }
        },
        cardContent = { card ->
            val quantity = deck.items.firstOrNull { it.cardId == card.id }?.quantity ?: 0
            val addLabel = stringResource(R.string.add_card_copy, card.name)
            val removeLabel = stringResource(R.string.remove_card_copy, card.name)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CardPreviewTile(card = card, modifier = Modifier.fillMaxWidth())
                Text(stringResource(R.string.deck_card_copies, quantity))
                validation?.ramWarnings?.firstOrNull { it.cardId == card.id }?.let { warning ->
                    CardRamWarningText(warning)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onRemoveCard(card.id) },
                        enabled = quantity > 0,
                        modifier = Modifier.weight(1f).semantics { contentDescription = removeLabel },
                        contentPadding = PaddingValues(8.dp),
                    ) { Text("−") }
                    OutlinedButton(
                        onClick = { onAddCard(card.id) },
                        enabled = canAddCard(card.id),
                        modifier = Modifier.weight(1f).semantics { contentDescription = addLabel },
                        contentPadding = PaddingValues(8.dp),
                    ) { Text("+") }
                }
            }
        },
    )
}
