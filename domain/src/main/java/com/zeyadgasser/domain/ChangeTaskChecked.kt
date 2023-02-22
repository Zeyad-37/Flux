package com.zeyadgasser.domain

class FluxTaskUseCases {
    fun onChangeTaskChecked(
        tasks: List<CheckableItem>,
        fluxTask: CheckableItem,
        checked: Boolean
    ): Unit? = tasks.find { it.id == fluxTask.id }?.let { task -> task.checked = checked }

    fun removeTask(
        tasks: MutableList<CheckableItem>, fluxTask: CheckableItem
    ): Boolean = tasks.remove(fluxTask)
}
