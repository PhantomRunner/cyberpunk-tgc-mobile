package com.example.cyberpunktcg.ui.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cyberpunktcg.R
import com.example.cyberpunktcg.data.model.Deck

@Composable
internal fun MyDecksScreen(
    decks: List<Deck>,
    onCreateDeck: (String) -> Unit,
    onOpenDeck: (String) -> Unit,
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var newDeckName by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            stringResource(R.string.deck_session_notice),
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(
            onClick = {
                newDeckName = ""
                showCreateDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.new_deck))
        }
        if (decks.isEmpty()) {
            Text(stringResource(R.string.no_decks_yet))
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(decks, key = { it.id }) { deck ->
                    OutlinedCard(
                        onClick = { onOpenDeck(deck.id) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                deck.name,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(stringResource(R.string.deck_draft), style = MaterialTheme.typography.labelLarge)
                            Text(stringResource(R.string.deck_main_count, deck.mainCardCount))
                            Text(stringResource(R.string.deck_legend_count, deck.legendIds.size))
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        val submitDeck = {
            if (newDeckName.isNotBlank()) {
                val name = newDeckName
                showCreateDialog = false
                newDeckName = ""
                onCreateDeck(name)
            }
        }
        AlertDialog(
            onDismissRequest = {
                showCreateDialog = false
                newDeckName = ""
            },
            title = { Text(stringResource(R.string.new_deck)) },
            text = {
                OutlinedTextField(
                    value = newDeckName,
                    onValueChange = { newDeckName = it },
                    label = { Text(stringResource(R.string.deck_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submitDeck() }),
                )
            },
            confirmButton = {
                TextButton(onClick = submitDeck, enabled = newDeckName.isNotBlank()) {
                    Text(stringResource(R.string.create_deck))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCreateDialog = false
                    newDeckName = ""
                }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}
