package com.example.cyberpunktcg.ui.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.cyberpunktcg.R
import com.example.cyberpunktcg.data.model.Deck

@Composable
internal fun DeckEditorScreen(deck: Deck?, onBackToDecks: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (deck == null) {
            Text(stringResource(R.string.deck_not_found))
            Button(onClick = onBackToDecks) {
                Text(stringResource(R.string.back_to_my_decks))
            }
        } else {
            Text(deck.name, style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.deck_draft), style = MaterialTheme.typography.labelLarge)
            Text(stringResource(R.string.deck_session_notice))
            Text(stringResource(R.string.deck_main_count, deck.mainCardCount))
            Text(stringResource(R.string.deck_legend_count, deck.legendIds.size))
            Text(stringResource(R.string.empty_deck), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.deck_editing_unavailable))
        }
    }
}
