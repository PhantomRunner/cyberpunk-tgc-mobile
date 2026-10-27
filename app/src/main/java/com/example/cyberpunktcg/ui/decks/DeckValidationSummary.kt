package com.example.cyberpunktcg.ui.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.cyberpunktcg.R
import com.example.cyberpunktcg.data.model.CardColor
import com.example.cyberpunktcg.domain.CardRamWarning
import com.example.cyberpunktcg.domain.DeckValidation
import com.example.cyberpunktcg.ui.components.labelResId

@Composable
internal fun DeckValidationSummary(validation: DeckValidation) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.deck_ram_limits), style = MaterialTheme.typography.titleMedium)
            CardColor.entries.forEach { color ->
                Text(stringResource(R.string.deck_color_ram_limit, stringResource(color.labelResId), validation.ramLimits.getValue(color)))
            }
            Text(stringResource(R.string.deck_ram_explanation), style = MaterialTheme.typography.bodySmall)
            Text(stringResource(R.string.deck_basic_checks), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.deck_legend_count, validation.legendCount))
            Text(stringResource(R.string.deck_main_count_range, validation.mainCount))
            Text(stringResource(R.string.deck_validation_scope), style = MaterialTheme.typography.bodySmall)
            if (validation.hasLegendCountWarning) {
                ValidationWarning(stringResource(R.string.deck_legend_count_warning, validation.legendCount))
            }
            if (validation.hasMainCountWarning) {
                ValidationWarning(stringResource(R.string.deck_main_count_warning, validation.mainCount))
            }
            validation.duplicateLegendNames.forEach { name ->
                ValidationWarning(stringResource(R.string.deck_duplicate_legend_warning, name))
            }
            validation.copyWarnings.forEach { warning ->
                ValidationWarning(stringResource(R.string.deck_copy_limit_warning, warning.name, warning.subtitle, warning.quantity))
            }
            validation.legendsInMain.forEach { card ->
                ValidationWarning(stringResource(R.string.deck_legend_in_main_warning, card.name))
            }
            validation.ramWarnings.forEach { warning -> CardRamWarningText(warning) }
            if (!validation.hasWarnings) {
                Text(stringResource(R.string.deck_no_basic_warnings))
            }
        }
    }
}

@Composable
internal fun CardRamWarningText(warning: CardRamWarning) {
    ValidationWarning(stringResource(
        R.string.deck_card_ram_warning,
        warning.cardName,
        stringResource(warning.color.labelResId),
        warning.requiredRam,
        warning.availableRam,
    ))
}

@Composable
private fun ValidationWarning(text: String) {
    Text(text, color = MaterialTheme.colorScheme.error)
}
