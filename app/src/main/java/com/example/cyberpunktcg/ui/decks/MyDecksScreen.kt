package com.example.cyberpunktcg.ui.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
    onDeleteDeck: (String) -> Unit,
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var newDeckName by rememberSaveable { mutableStateOf("") }
    var pendingDeleteId by rememberSaveable { mutableStateOf<String?>(null) }

    val deckToDelete = decks.firstOrNull { it.id == pendingDeleteId }

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
                            val deleteLabel = stringResource(R.string.delete_named_deck, deck.name)
                            TextButton(
                                onClick = { pendingDeleteId = deck.id },
                                modifier = Modifier.align(Alignment.End).semantics { contentDescription = deleteLabel },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            ) {
                                Text(stringResource(R.string.delete_deck))
                            }
                        }
                    }
                }
            }
        }
    }

    if (deckToDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text(stringResource(R.string.delete_deck_title)) },
            text = {
                Text(
                    stringResource(R.string.delete_deck_message, deckToDelete.name),
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingDeleteId = null
                        onDeleteDeck(deckToDelete.id)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) { Text(stringResource(R.string.delete_deck)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) { Text(stringResource(R.string.cancel)) }
            },
        )
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
