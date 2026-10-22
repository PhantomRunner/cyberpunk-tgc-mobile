package com.example.cyberpunktcg.ui.collection

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.cyberpunktcg.R
import com.example.cyberpunktcg.ui.components.ScreenPlaceholder

@Composable
internal fun CollectionScreen(onOpenCard: () -> Unit) {
    ScreenPlaceholder {
        OutlinedButton(onClick = onOpenCard) {
            Text(stringResource(R.string.open_card_details))
        }
    }
}
