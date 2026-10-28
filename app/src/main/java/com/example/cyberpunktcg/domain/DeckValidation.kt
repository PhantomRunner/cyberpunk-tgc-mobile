package com.example.cyberpunktcg.domain

import com.example.cyberpunktcg.data.model.Card
import com.example.cyberpunktcg.data.model.CardColor
import com.example.cyberpunktcg.data.model.CardType
import com.example.cyberpunktcg.data.model.Deck
import com.example.cyberpunktcg.data.model.DeckItem

internal data class CardCopyWarning(val name: String, val subtitle: String, val quantity: Int)

internal data class CardRamWarning(
    val cardId: String,
    val cardName: String,
    val color: CardColor,
    val requiredRam: Int,
    val availableRam: Int,
)

internal data class DeckValidation(
    val legendCount: Int,
    val mainCount: Int,
    val ramLimits: Map<CardColor, Int>,
    val allowedColors: Set<CardColor>,
    val colorWarnings: List<Card>,
    val duplicateLegendNames: List<String>,
    val copyWarnings: List<CardCopyWarning>,
    val legendsInMain: List<Card>,
    val ramWarnings: List<CardRamWarning>,
) {
    val hasLegendCountWarning: Boolean get() = legendCount != 3
    val hasMainCountWarning: Boolean get() = mainCount !in 40..50
    val hasWarnings: Boolean
        get() = hasLegendCountWarning || hasMainCountWarning || duplicateLegendNames.isNotEmpty() ||
            copyWarnings.isNotEmpty() || legendsInMain.isNotEmpty() || colorWarnings.isNotEmpty() || ramWarnings.isNotEmpty()
}

internal fun selectedLegendColors(deck: Deck, cards: List<Card>): Set<CardColor> {
    return cards.filter { it.type == CardType.LEGEND && it.id in deck.legendIds }.map { it.color }.toSet()
}

internal fun calculateRamLimits(legends: List<Card>): Map<CardColor, Int> {
    return CardColor.entries.associateWith { color ->
        legends.filter { it.type == CardType.LEGEND && it.color == color }.sumOf { it.ram }
    }
}

internal fun countCardCopies(items: List<DeckItem>, cards: List<Card>, card: Card): Int {
    val matchingIds = cards.filter {
        it.type != CardType.LEGEND && it.name == card.name && it.subtitle == card.subtitle
    }.map { it.id }
    return items.filter { it.cardId in matchingIds }.sumOf { it.quantity }
}

internal fun validateDeck(deck: Deck, cards: List<Card>): DeckValidation {
    val cardsById = cards.associateBy { it.id }
    val legends = deck.legendIds.mapNotNull { cardsById[it] }.filter { it.type == CardType.LEGEND }
    val mainCards = deck.items.mapNotNull { cardsById[it.cardId] }
        .filter { it.type != CardType.LEGEND }.distinctBy { it.id }
    val limits = calculateRamLimits(legends)
    val allowedColors = selectedLegendColors(deck, cards)
    val mainCount = deck.items.filter { item ->
        val card = cardsById[item.cardId]
        card != null && card.type != CardType.LEGEND
    }.sumOf { it.quantity }
    val copyWarnings = mainCards.distinctBy { it.name to it.subtitle }.mapNotNull { card ->
        val quantity = countCardCopies(deck.items, cards, card)
        if (quantity > 3) CardCopyWarning(card.name, card.subtitle, quantity) else null
    }

    val ramWarnings = mainCards.mapNotNull { card ->
        val available = limits.getValue(card.color)
        if (card.ram > available) {
            CardRamWarning(card.id, card.name, card.color, card.ram, available)
        } else null
    }
    return DeckValidation(
        legendCount = legends.size,
        mainCount = mainCount,
        ramLimits = limits,
        allowedColors = allowedColors,
        colorWarnings = mainCards.filter { it.color !in allowedColors },
        duplicateLegendNames = legends.groupingBy { it.name }.eachCount().filterValues { it > 1 }.keys.toList(),
        copyWarnings = copyWarnings,
        legendsInMain = deck.items.mapNotNull { cardsById[it.cardId] }
            .filter { it.type == CardType.LEGEND }.distinctBy { it.id },
        ramWarnings = ramWarnings,
    )
}
