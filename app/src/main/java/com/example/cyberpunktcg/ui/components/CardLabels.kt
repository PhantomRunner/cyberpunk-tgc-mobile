package com.example.cyberpunktcg.ui.components

import androidx.annotation.StringRes
import com.example.cyberpunktcg.R
import com.example.cyberpunktcg.data.model.CardType

@get:StringRes
internal val CardType.labelResId: Int
    get() = when (this) {
        CardType.LEGEND -> R.string.card_type_legend
        CardType.UNIT -> R.string.card_type_unit
        CardType.PROGRAM -> R.string.card_type_program
        CardType.GEAR -> R.string.card_type_gear
    }
