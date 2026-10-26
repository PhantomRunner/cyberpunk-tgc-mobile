package com.example.cyberpunktcg.ui.components

import androidx.annotation.StringRes
import com.example.cyberpunktcg.R
import com.example.cyberpunktcg.data.model.CardColor
import com.example.cyberpunktcg.data.model.CardType

@get:StringRes
internal val CardType.labelResId: Int
    get() = when (this) {
        CardType.LEGEND -> R.string.card_type_legend
        CardType.UNIT -> R.string.card_type_unit
        CardType.PROGRAM -> R.string.card_type_program
        CardType.GEAR -> R.string.card_type_gear
    }

@get:StringRes
internal val CardColor.labelResId: Int
    get() = when (this) {
        CardColor.RED -> R.string.card_color_red
        CardColor.YELLOW -> R.string.card_color_yellow
        CardColor.BLUE -> R.string.card_color_blue
        CardColor.GREEN -> R.string.card_color_green
    }
