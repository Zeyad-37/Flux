package com.zeyadgasser.core.api

/**
 * To indicate that there are ongoing background calculations, the framework emits [Progress] objects for
 * the view to react appropriately
 */
data class Progress constructor(val isLoading: Boolean, val input: Input) : Output
