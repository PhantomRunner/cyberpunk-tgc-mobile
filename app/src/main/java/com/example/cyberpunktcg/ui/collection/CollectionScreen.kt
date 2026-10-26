package com.example.cyberpunktcg.ui.collection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.cyberpunktcg.R
import com.example.cyberpunktcg.data.model.Card
import com.example.cyberpunktcg.data.model.CardColor
import com.example.cyberpunktcg.data.model.CardType
import com.example.cyberpunktcg.ui.components.CardPreviewTile
import com.example.cyberpunktcg.ui.components.labelResId

@Composable
internal fun CollectionScreen(
    cards: List<Card>,
    state: CollectionUiState,
    onQueryChange: (String) -> Unit,
    onTypesChange: (Set<CardType>) -> Unit,
    onColorsChange: (Set<CardColor>) -> Unit,
    onSortFieldChange: (CardSortField) -> Unit,
    onSortDirectionChange: (SortDirection) -> Unit,
    onOpenCard: (String) -> Unit,
) {
    val gridState = rememberLazyGridState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var previousState by remember { mutableStateOf(state) }
    LaunchedEffect(state) {
        // Back restores the grid position; only a change of conditions resets it.
        if (state != previousState) {
            previousState = state
            gridState.scrollToItem(0)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(start = 16.dp, top = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.search_by_name)) },
            trailingIcon = {
                if (state.query.isNotEmpty()) {
                    TextButton(onClick = { onQueryChange("") }) {
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TypeFilterMenu(
                selectedTypes = state.selectedTypes,
                onTypesChange = onTypesChange,
                modifier = Modifier.weight(1f),
            )
            ColorFilterMenu(
                selectedColors = state.selectedColors,
                onColorsChange = onColorsChange,
                modifier = Modifier.weight(1f),
            )
            CollectionSortMenu(
                field = state.sortField,
                direction = state.sortDirection,
                onFieldChange = onSortFieldChange,
                onDirectionChange = onSortDirectionChange,
                modifier = Modifier.weight(1f),
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            state = gridState,
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.demo_collection),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = stringResource(R.string.demo_collection_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (cards.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(stringResource(R.string.no_cards_found))
                }
            }
            items(items = cards, key = { card -> card.id }) { card ->
                CardPreviewTile(
                    card = card,
                    onClick = { onOpenCard(card.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun TypeFilterMenu(
    selectedTypes: Set<CardType>,
    onTypesChange: (Set<CardType>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier) {
        OutlinedButton(
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
                expanded = true
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(8.dp),
        ) {
            val summary = if (selectedTypes.isEmpty()) stringResource(R.string.all_filter_options)
                else selectedTypes.size.toString()
            Text(stringResource(R.string.card_types_filter, summary))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.all_card_types)) },
                onClick = { onTypesChange(emptySet()) },
            )
            CardType.entries.forEach { type ->
                val selected = type in selectedTypes
                DropdownMenuItem(
                    text = { Text(stringResource(type.labelResId)) },
                    modifier = Modifier.semantics {
                        role = Role.Checkbox
                        toggleableState = if (selected) ToggleableState.On else ToggleableState.Off
                    },
                    leadingIcon = { Checkbox(checked = selected, onCheckedChange = null) },
                    onClick = {
                        onTypesChange(if (selected) selectedTypes - type else selectedTypes + type)
                    },
                )
            }
        }
    }
}

@Composable
private fun ColorFilterMenu(
    selectedColors: Set<CardColor>,
    onColorsChange: (Set<CardColor>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier) {
        OutlinedButton(
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
                expanded = true
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(8.dp),
        ) {
            val summary = if (selectedColors.isEmpty()) stringResource(R.string.all_filter_options)
                else selectedColors.size.toString()
            Text(stringResource(R.string.card_colors_filter, summary))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.all_card_colors)) },
                onClick = { onColorsChange(emptySet()) },
            )
            CardColor.entries.forEach { color ->
                val selected = color in selectedColors
                DropdownMenuItem(
                    text = { Text(stringResource(color.labelResId)) },
                    modifier = Modifier.semantics {
                        role = Role.Checkbox
                        toggleableState = if (selected) ToggleableState.On else ToggleableState.Off
                    },
                    leadingIcon = { Checkbox(checked = selected, onCheckedChange = null) },
                    onClick = {
                        onColorsChange(if (selected) selectedColors - color else selectedColors + color)
                    },
                )
            }
        }
    }
}
