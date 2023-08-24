package com.zeyadgasser.composables.presentationModels

import android.os.Parcelable
import com.zeyadgasser.domainPure.FluxTask
import kotlinx.parcelize.Parcelize

@Parcelize
data class FluxTaskItem(val id: Long, val label: String, val checked: Boolean = false) : Parcelable {
    constructor(fluxTask: FluxTask) : this(fluxTask.id, fluxTask.label, fluxTask.checked)
}
