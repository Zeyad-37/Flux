package com.zeyadgasser.domain

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.parcelize.Parcelize

@Parcelize
class FluxTask(
    override val id: Long,
    val label: String,
    private val initialChecked: Boolean = false
) : CheckableItem, Parcelable {
    override var checked by mutableStateOf(initialChecked)
}
