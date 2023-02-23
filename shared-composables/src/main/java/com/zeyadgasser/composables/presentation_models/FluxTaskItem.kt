package com.zeyadgasser.composables.presentation_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FluxTaskItem(val id: Long, val label: String, val checked: Boolean = false) : Parcelable
