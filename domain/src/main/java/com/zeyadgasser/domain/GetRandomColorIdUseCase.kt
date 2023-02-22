package com.zeyadgasser.domain

import com.zeyadgasser.flux.domain.R
import kotlin.random.Random

class GetRandomColorIdUseCase {
    fun getRandomColorId(): Int = when (Random.nextInt(10)) {
        0 -> R.color.purple_200
        1 -> android.R.color.holo_orange_dark
        2 -> R.color.teal_200
        3 -> R.color.teal_700
        4 -> androidx.appcompat.R.color.material_blue_grey_800
        5 -> android.R.color.holo_green_light
        6 -> android.R.color.darker_gray
        7 -> android.R.color.holo_red_light
        8 -> android.R.color.holo_blue_dark
        9 -> android.R.color.holo_green_dark
        else -> android.R.color.black
    }
}
