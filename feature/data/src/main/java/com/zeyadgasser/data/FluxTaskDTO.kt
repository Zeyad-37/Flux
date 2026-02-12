package com.zeyadgasser.data

import com.zeyadgasser.domainPure.FluxTask

data class FluxTaskDTO(
    val id: Long, val label: String, val checked: Boolean = false
) : MapsTo<FluxTask> {
    override fun map(): FluxTask = FluxTask(id, label, checked)
}
