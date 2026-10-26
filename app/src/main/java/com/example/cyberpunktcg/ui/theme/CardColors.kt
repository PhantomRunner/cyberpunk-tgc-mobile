package com.example.cyberpunktcg.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.cyberpunktcg.data.model.CardColor


internal val CardColor.frameColor: Color
    get() = when (this) {
        CardColor.RED -> Color(0xFFD64550)
        CardColor.YELLOW -> Color(0xFFA67C00)
        CardColor.BLUE -> Color(0xFF357AC4)
        CardColor.GREEN -> Color(0xFF27844A)
    }
