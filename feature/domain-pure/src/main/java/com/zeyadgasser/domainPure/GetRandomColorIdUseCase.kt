package com.zeyadgasser.domainPure

import kotlin.random.Random

object GetRandomColorIdUseCase {
    private const val THREE = 3
    private const val FOUR = 4
    private const val FIVE = 5
    private const val SIX = 6
    private const val SEVEN = 7
    private const val EIGHT = 8
    private const val NINE = 9
    private const val TEN = 10
    fun getRandomColorId(): Long = when (Random.nextInt(TEN)) {
        0 -> PURPLE_200
        1 -> ORANGE_DARK
        2 -> TEAL_200
        THREE -> TEAL_700
        FOUR -> BLUE_GREY_800
        FIVE -> GREEN_LIGHT
        SIX -> DARKER_GRAY
        SEVEN -> RED_LIGHT
        EIGHT -> BLUE_DARK
        NINE -> GREEN_DARK
        else -> BLACK
    }
}
