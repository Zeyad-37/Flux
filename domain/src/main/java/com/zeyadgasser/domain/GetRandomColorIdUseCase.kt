package com.zeyadgasser.domain

import com.zeyadgasser.flux.domain.R
import kotlin.random.Random

class GetRandomColorIdUseCase {
//    fun getRandomColorId(): Int = when (Random.nextInt(10)) {
//        0 -> R.color.purple_200
//        1 -> android.R.color.holo_orange_dark
//        2 -> R.color.teal_200
//        3 -> R.color.teal_700
//        4 -> androidx.appcompat.R.color.material_blue_grey_800
//        5 -> android.R.color.holo_green_light
//        6 -> android.R.color.darker_gray
//        7 -> android.R.color.holo_red_light
//        8 -> android.R.color.holo_blue_dark
//        9 -> android.R.color.holo_green_dark
//        else -> android.R.color.black
//    }

    fun getRandomColorId(): Long = when (Random.nextInt(10)) {
        0 -> 0xFFBB86FC // purple_200
        1 -> 0xffff8800 // holo_orange_dark
        2 -> 0xFF03DAC5 // teal_200
        3 -> 0xFF018786 // teal_700
        4 -> 0xff37474f // material_blue_grey_800
        5 -> 0xff99cc00 // holo_green_light
        6 -> 0xaaa // darker_gray
        7 -> 0xffff4444 // holo_red_light
        8 -> 0xff0099cc // holo_blue_dark
        9 -> 0xff669900 // holo_green_dark
        else -> 0xFF000000 // black
    }
}
