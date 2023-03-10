package com.zeyadgasser.data

private const val TASK_LIST_SIZE = 37

class FluxTaskAPI {
    private var fluxTasks =
        MutableList(TASK_LIST_SIZE) { i -> FluxTaskDTO(i.toLong(), "Task # $i") }

    fun getFluxTasks(): List<FluxTaskDTO> = fluxTasks.toList()

    fun removeTask(id: Long): List<FluxTaskDTO> {
        val tempList = mutableListOf<FluxTaskDTO>()
        fluxTasks.forEach { if (it.id != id) tempList.add(it) }
        fluxTasks = tempList
        return fluxTasks
    }

    fun onChangeTaskChecked(id: Long, checked: Boolean): List<FluxTaskDTO> =
        fluxTasks.map { task -> if (task.id == id) task.copy(checked = checked) else task }
            .run {
                fluxTasks = toMutableList()
                getFluxTasks()
            }
}
