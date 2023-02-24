package com.zeyadgasser.domain_pure

import kotlin.random.Random

object GetRandomColorIdUseCase {
    fun getRandomColorId(): Long = when (Random.nextInt(10)) {
        0 -> Purple200
        1 -> OrangeDark
        2 -> Teal200
        3 -> Teal700
        4 -> BlueGrey800
        5 -> GreenLight
        6 -> DarkerGray
        7 -> RedLight
        8 -> BlueDark
        9 -> GreenDark
        else -> Black
    }
}
