package com.zeyadgasser.composables.presentationModels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FluxTaskItem(val id: Long, val label: String, val checked: Boolean = false) : Parcelable
