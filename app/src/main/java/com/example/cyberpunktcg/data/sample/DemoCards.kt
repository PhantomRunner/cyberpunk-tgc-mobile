package com.example.cyberpunktcg.data.sample

import com.example.cyberpunktcg.data.model.Card

object DemoCards {
    val cards: List<Card> = listOf(
        Card(id = "demo1", name = "Test 0", type = "Character"),
        Card(id = "demo2", name = "Test 1", type = "Character"),
        Card(id = "demo3", name = "Test 2", type = "Equipment"),
        Card(id = "demo4", name = "Test 3", type = "Program"),
        Card(id = "demo5", name = "Test 4", type = "Event"),
        Card(id = "demo6", name = "Test 5", type = "Character"),
    )
}
