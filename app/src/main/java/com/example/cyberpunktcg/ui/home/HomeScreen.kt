package com.example.cyberpunktcg.ui.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cyberpunktcg.R

@Composable
internal fun HomeScreen(
    onBrowseCollection: () -> Unit,
    onOpenDecks: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HomeStatistic(R.string.home_total_cards, Modifier.weight(1f).fillMaxHeight())
            HomeStatistic(R.string.home_unique_cards, Modifier.weight(1f).fillMaxHeight())
            HomeStatistic(R.string.home_complete_sets, Modifier.weight(1f).fillMaxHeight())
        }
        Text(
            text = stringResource(R.string.home_statistics_notice),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = onBrowseCollection, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.home_browse_collection))
        }
        Button(onClick = onOpenDecks, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.my_decks))
        }
        Text(
            text = stringResource(R.string.demo_collection_description),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = stringResource(R.string.deck_session_notice),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun HomeStatistic(@StringRes labelResId: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Owned quantities and set completion are not available in the demo catalog.
            Text(stringResource(R.string.home_statistic_placeholder), style = MaterialTheme.typography.headlineMedium)
            Text(
                text = stringResource(labelResId),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
