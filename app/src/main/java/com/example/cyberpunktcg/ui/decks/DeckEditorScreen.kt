package com.example.cyberpunktcg.ui.decks

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.cyberpunktcg.R
import com.example.cyberpunktcg.data.model.Card
import com.example.cyberpunktcg.data.model.Deck
import com.example.cyberpunktcg.ui.components.labelResId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DeckEditorScreen(
    deck: Deck?,
    onBack: () -> Unit,
    legends: List<Card>,
    mainCards: List<Card>,
    onAddCards: () -> Unit,
    hasUnsavedChanges: Boolean,
    onNameChange: (String) -> Unit,
    canAddLegend: (String) -> Boolean,
    onAddLegend: (String) -> Unit,
    onRemoveLegend: (String) -> Unit,
    onSaveDraft: () -> Unit,
    onBackToDecks: () -> Unit,
) {

    BackHandler(onBack = onBack)
    var showLegendPicker by rememberSaveable(deck?.id) { mutableStateOf(false) }
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
            Text(stringResource(R.string.deck_draft), style = MaterialTheme.typography.labelLarge)
            Text(stringResource(R.string.deck_session_notice))
            OutlinedTextField(
                value = deck.name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.deck_name)) },
                singleLine = true,
                isError = deck.name.isBlank(),
                supportingText = if (deck.name.isBlank()) {
                    { Text(stringResource(R.string.deck_name_required)) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = onSaveDraft,
                enabled = deck.name.isNotBlank() && hasUnsavedChanges,
                modifier = Modifier.fillMaxWidth(),
            ) { Text(stringResource(R.string.save_draft)) }
            Text(stringResource(if (hasUnsavedChanges) R.string.unsaved_changes else R.string.draft_saved))
            Text(stringResource(R.string.deck_legend_count, deck.legendIds.size))
            repeat(3) { index ->
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(stringResource(R.string.legend_slot, index + 1), style = MaterialTheme.typography.titleSmall)
                        val legendId = deck.legendIds.getOrNull(index)
                        val legend = legends.firstOrNull { it.id == legendId }
                        if (legend != null) {
                            Text(legend.name, style = MaterialTheme.typography.titleMedium)
                            Text(legend.subtitle)
                            Text(stringResource(legend.color.labelResId))
                            Text(stringResource(R.string.card_ram, legend.ram))
                            TextButton(onClick = { onRemoveLegend(legend.id) }) {
                                Text(stringResource(R.string.remove_legend))
                            }
                        } else {
                            OutlinedButton(onClick = { showLegendPicker = true }) {
                                Text(stringResource(R.string.choose_legend))
                            }
                        }
                    }
                }
            }
            Text(stringResource(R.string.deck_main_count, deck.mainCardCount))
            Button(onClick = onAddCards, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.add_cards))
            }
            if (deck.items.isEmpty()) {
                Text(stringResource(R.string.no_main_cards))
            }
            deck.items.forEach { item ->
                val card = mainCards.firstOrNull { it.id == item.cardId }
                if (card != null) {
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(card.name, style = MaterialTheme.typography.titleMedium)
                            Text(card.subtitle)
                            Text(stringResource(card.type.labelResId))
                            Text(stringResource(R.string.deck_card_copies, item.quantity))
                        }
                    }
                }
            }
        }
    }
    if (showLegendPicker && deck != null) {
        ModalBottomSheet(
            onDismissRequest = { showLegendPicker = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            // Android 7 cannot draw dark navigation icons on the light sheet.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                val view = LocalView.current
                SideEffect {
                    @Suppress("DEPRECATION")
                    (view.parent as? DialogWindowProvider)?.window?.navigationBarColor = android.graphics.Color.BLACK
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(stringResource(R.string.choose_legend), style = MaterialTheme.typography.titleLarge)
                    Text(stringResource(R.string.legend_selection_hint))
                }
                items(legends, key = { it.id }) { legend ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(legend.name, style = MaterialTheme.typography.titleMedium)
                            Text(legend.subtitle)
                            Text(stringResource(legend.color.labelResId))
                            Text(stringResource(R.string.card_ram, legend.ram))
                            Button(
                                enabled = canAddLegend(legend.id),
                                onClick = {
                                    onAddLegend(legend.id)
                                    showLegendPicker = false
                                },
                            ) { Text(stringResource(R.string.select_legend)) }
                        }
                    }
                }
                item {
                    TextButton(onClick = { showLegendPicker = false }, modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        }
    }
}
