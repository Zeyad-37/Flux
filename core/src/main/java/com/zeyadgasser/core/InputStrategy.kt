package com.zeyadgasser.core

private const val THROTTLE_INTERVAL = 200L
private const val DEBOUNCE_INTERVAL = 500L

enum class InputStrategy(val interval: Long) {
    NONE(0L), THROTTLE(THROTTLE_INTERVAL), DEBOUNCE(DEBOUNCE_INTERVAL)
}
