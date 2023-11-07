package com.zeyadgasser.core

import android.os.Parcelable

/**
 * All emissions presented to the view implement this [Output] interface.
 */
sealed interface Output : Loggable

/**
 * All states presented to the view and produced/reduced by the VM must implement this [State] interface.
 */
interface State : Parcelable, Result, Output

/**
 * All effects presented to the view and produced by the VM must implement this [Effect] interface.
 */
interface Effect : Result, Output

/**
 * All caught exceptions presented to the view and produced by the VM must be wrapped in this [Error] class.
 */
data class Error(val message: String, val cause: Throwable, val input: Input = EmptyInput) : Result, Output

/**
 * To indicate that there are ongoing background calculations, the framework emits [Progress] objects for
 * the view to react appropriately
 */
data class Progress(val isLoading: Boolean, val input: Input) : Result, Output
