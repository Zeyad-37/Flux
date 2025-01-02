package com.zeyadgasser.core.v2

/**
 * An [Effect] is a special type of [Result] that represents a one off action (e.g navigation)
 *
 * [Effect]'s are presented to the view immediately rather than being reduced
 */
interface Effect : Result
