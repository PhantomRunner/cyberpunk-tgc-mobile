package com.example.cyberpunktcg.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cyberpunktcg.R
import com.example.cyberpunktcg.data.model.Card
import com.example.cyberpunktcg.data.model.Deck
import com.example.cyberpunktcg.ui.theme.frameColor

@Composable
internal fun HomeDeckCard(deck: Deck, legends: List<Card>, onClick: () -> Unit) {
    OutlinedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = deck.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(stringResource(R.string.deck_draft), style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.deck_main_count, deck.mainCardCount), modifier = Modifier.weight(1f))
                Text(stringResource(R.string.deck_legend_count, deck.legendIds.size), modifier = Modifier.weight(1f))
            }
            if (legends.isEmpty()) {
                Text(stringResource(R.string.no_legends_selected), style = MaterialTheme.typography.bodySmall)
            } else {
                val shownLegends = legends.take(3)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    shownLegends.forEach { card ->
                        LegendPreview(card, Modifier.weight(1f))
                    }

                    repeat(3 - shownLegends.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun LegendPreview(card: Card, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(2.dp, card.color.frameColor),
    ) {
        Box {
            Image(
                painter = painterResource(R.drawable.card_art_placeholder),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline),
                alpha = 0.35f,
            )
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(min = 96.dp).padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(stringResource(R.string.art_placeholder), style = MaterialTheme.typography.labelSmall)
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
