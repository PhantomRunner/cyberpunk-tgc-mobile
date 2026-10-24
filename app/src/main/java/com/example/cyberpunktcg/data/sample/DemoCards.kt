package com.example.cyberpunktcg.data.sample

import com.example.cyberpunktcg.data.model.Card

object DemoCards {
    val cards: List<Card> = listOf(
        Card(
            id = "demo1", name = "Test 0", type = "Character",
            cost = 3, power = 4, ram = 2,
            subtitle = "test",
            unitTypes = listOf("1", "2"),
            description = "something\n" +
                "something else",
        ),
        Card(
            id = "demo2", name = "Test 1", type = "Character",
            cost = 2, power = 1, ram = 0,
            subtitle = "test",
            unitTypes = listOf("45454ddfsf"),
            description = "test\n" +
                "testone\n" +
                "testtwo" +
                "testthree",
        ),
        Card(
            id = "demo3", name = "Test 2", type = "Equipment",
            cost = 1, power = 0, ram = 0,
            subtitle = "test",
            unitTypes = emptyList(),
            description = "test",
        ),
        Card(
            id = "demo4", name = "Test 3", type = "Program",
            cost = 0, power = 0, ram = 3,
            subtitle = "test",
            unitTypes = emptyList(),
            description = "test",
        ),
        Card(
            id = "demo5", name = "Test 4", type = "Event",
            cost = 2, power = 0, ram = 1,
            subtitle = "test",
            unitTypes = emptyList(),
            description = "test",
        ),
        Card(
            id = "demo6", name = "Test 5", type = "Character",
            cost = 5, power = 6, ram = 2,
            subtitle = "test",
            unitTypes = listOf("test", "test"),
            description = "test\n" +
                "test",
        ),
    )
}
