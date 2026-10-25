package com.example.cyberpunktcg.ui.collection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.cyberpunktcg.R
import com.example.cyberpunktcg.data.model.Card
import com.example.cyberpunktcg.ui.components.CardTile

@Composable
internal fun CollectionScreen(cards: List<Card>, onOpenCard: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }

    var selectedType by rememberSaveable { mutableStateOf<String?>(null) }
    var isTypeMenuExpanded by remember { mutableStateOf(false) }
    val cardTypes = cards.map { it.type }.distinct().sorted()
    val matchingCards = cards.filter { card ->
        card.name.contains(query.trim(), ignoreCase = true) &&
            (selectedType == null || card.type == selectedType)
    }
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(query, selectedType) {
        listState.scrollToItem(0)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.demo_collection),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(R.string.demo_collection_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.search_by_name)) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    TextButton(onClick = { query = "" }) {
                        Text(stringResource(R.string.clear_search))
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                keyboardController?.hide()
                focusManager.clearFocus()
            }),
        )
        Box {
            OutlinedButton(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    isTypeMenuExpanded = true
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    stringResource(
                        R.string.card_type_filter,
                        selectedType ?: stringResource(R.string.all_card_types),
                    ),
                )
            }
            DropdownMenu(
                expanded = isTypeMenuExpanded,
                onDismissRequest = { isTypeMenuExpanded = false },
            ) {
                val options: List<String?> = listOf(null) + cardTypes
                options.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type ?: stringResource(R.string.all_card_types)) },
                        leadingIcon = { RadioButton(selected = selectedType == type, onClick = null) },
                        onClick = {
                            selectedType = type
                            isTypeMenuExpanded = false
                        },
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (matchingCards.isEmpty()) {
                item {
                    Text(stringResource(R.string.no_cards_found))
                }
            }
            items(items = matchingCards, key = { card -> card.id }) { card ->
                CardTile(card = card, modifier = Modifier.fillMaxWidth())
            }
            item {
                OutlinedButton(onClick = onOpenCard) {
                    Text(stringResource(R.string.open_card_details))
                }
            }
        }
    }
}
