package com.example.snakegamedacs3

import kotlin.random.Random


fun generateFood(snake: List<Pair<Int, Int>>, rows: Int, columns: Int): Pair<Int, Int> {
    var newFood: Pair<Int, Int>
    do {
        newFood = Pair(Random.nextInt(columns), Random.nextInt(rows))
    } while (newFood in snake)
    return newFood
}
