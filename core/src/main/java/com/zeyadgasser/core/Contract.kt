package com.zeyadgasser.core

import android.os.Parcelable

sealed interface Loggable

open class Input(val showProgress: Boolean = true) : Loggable

interface Result

sealed interface Output

interface State : Parcelable, Output

data class Error(val message: String, val cause: Throwable, val input: Input? = null) : Output

interface Effect : Output

data class Progress(val isLoading: Boolean, val input: Input) : Output
