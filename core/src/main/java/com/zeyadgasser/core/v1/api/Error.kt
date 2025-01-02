package com.zeyadgasser.core.v1.api

/**
 * All caught exceptions presented to the view and produced by the VM must be wrapped in this [Error] class.
 */
data class Error internal constructor(val message: String, val cause: Throwable, val input: Input = EmptyInput) : Output
