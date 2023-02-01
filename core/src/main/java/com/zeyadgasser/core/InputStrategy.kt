package com.zeyadgasser.core

enum class InputStrategy(val interval: Long) {
    NONE(0L), THROTTLE(200L), DEBOUNCE(500L)
}
