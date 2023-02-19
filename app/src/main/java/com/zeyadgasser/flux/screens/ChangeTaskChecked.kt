package com.zeyadgasser.flux.screens

import com.zeyadgasser.flux.screens.mvi.FluxTask

class FluxTaskUseCases {
    fun onChangeTaskChecked(tasks: List<FluxTask>, fluxTask: FluxTask, checked: Boolean): Unit? =
        tasks.find { it.id == fluxTask.id }?.let { task -> task.checked = checked }

    fun removeTask(tasks: MutableList<FluxTask>, fluxTask: FluxTask): Boolean =
        tasks.remove(fluxTask)
}