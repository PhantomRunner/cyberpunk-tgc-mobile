package com.example.cyberpunktcg.ui.decks

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.cyberpunktcg.R
import com.example.cyberpunktcg.ui.components.ScreenPlaceholder

@Composable
internal fun MyDecksScreen(onOpenDeck: () -> Unit) {
    ScreenPlaceholder {
        OutlinedButton(onClick = onOpenDeck) {
            Text(stringResource(R.string.open_deck_editor))
        }
    }
}
