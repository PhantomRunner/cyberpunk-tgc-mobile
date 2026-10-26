package com.example.cyberpunktcg.data.model

data class Card(
    val id: String,
    val name: String,
    val type: CardType,
    val cost: Int,
    val power: Int,
    val ram: Int,
    val subtitle: String,
    val unitTypes: List<String>,
    val description: String,
)
