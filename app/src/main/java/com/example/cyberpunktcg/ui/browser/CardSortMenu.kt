package com.example.cyberpunktcg.ui.browser

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.cyberpunktcg.R

@Composable
internal fun CardSortMenu(
    field: CardSortField,
    direction: SortDirection,
    onFieldChange: (CardSortField) -> Unit,
    onDirectionChange: (SortDirection) -> Unit,
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
            Text(
                stringResource(
                    R.string.collection_sort,
                    stringResource(field.labelResId),
                    if (direction == SortDirection.ASCENDING) "↑" else "↓",
                ),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            CardSortField.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(stringResource(option.labelResId)) },
                    modifier = Modifier.semantics {
                        role = Role.RadioButton
                        selected = option == field
                    },
                    leadingIcon = { RadioButton(selected = option == field, onClick = null) },
                    onClick = { onFieldChange(option) },
                )
            }
            HorizontalDivider()
            SortDirection.entries.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(stringResource(
                            if (option == SortDirection.ASCENDING) R.string.sort_ascending
                            else R.string.sort_descending,
                        ))
                    },
                    modifier = Modifier.semantics {
                        role = Role.RadioButton
                        selected = option == direction
                    },
                    leadingIcon = { RadioButton(selected = option == direction, onClick = null) },
                    onClick = { onDirectionChange(option) },
                )
            }
        }
    }
}

@get:StringRes
private val CardSortField.labelResId: Int
    get() = when (this) {
        CardSortField.NAME -> R.string.sort_name
        CardSortField.COST -> R.string.sort_cost
        CardSortField.POWER -> R.string.sort_power
        CardSortField.RAM -> R.string.sort_ram
        CardSortField.COLOR -> R.string.sort_color
        CardSortField.TYPE -> R.string.sort_type
    }
