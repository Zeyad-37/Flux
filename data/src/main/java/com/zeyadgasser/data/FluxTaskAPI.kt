package com.zeyadgasser.data

private const val TASK_LIST_SIZE = 30

class FluxTaskAPI {
    private var fluxTasks =
        MutableList(TASK_LIST_SIZE) { i -> FluxTaskDTO(i.toLong(), "Task # $i") }

    fun getFluxTasks() = fluxTasks

    fun removeTask(id: Long): List<FluxTaskDTO> =
        if (fluxTasks.removeIf { it.id == id }) fluxTasks else error("Couldn't remove task!")

    fun onChangeTaskChecked(id: Long, checked: Boolean): List<FluxTaskDTO> =
        fluxTasks.map { task -> if (task.id == id) task.copy(checked = checked) else task }
            .apply { fluxTasks = toMutableList() }
}
