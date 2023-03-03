package com.zeyadgasser.data

import com.zeyadgasser.domainPure.FluxTask
import com.zeyadgasser.domainPure.FluxTaskRepository
import javax.inject.Inject

private const val TASK_LIST_SIZE = 30

class FluxTaskRepositoryImpl @Inject constructor(
    private val fluxTaskDTOMapper: FluxTaskDTOMapper
) : FluxTaskRepository {

    private var tasks = MutableList(TASK_LIST_SIZE) { i -> FluxTaskDTO(i.toLong(), "Task # $i") }

    override fun getFluxTasks(): List<FluxTask> = tasks.toList().map { fluxTaskDTOMapper.map(it) }

    override fun removeTask(id: Long): List<FluxTask> =
        if (tasks.removeIf { it.id == id }) tasks.map { fluxTaskDTOMapper.map(it) }
        else error("Couldn't remove task!")

    override fun onChangeTaskChecked(id: Long, checked: Boolean): List<FluxTask> =
        tasks.map { task -> if (task.id == id) task.copy(checked = checked) else task }
            .apply { tasks = this.toMutableList() }.map { fluxTaskDTOMapper.map(it) }
}
