package com.zeyadgasser.domain_pure

import javax.inject.Inject

//class FluxTaskUseCases @Inject constructor(private val fluxTaskRepository: FluxTaskRepository) { //FIXME
class FluxTaskUseCases(private val fluxTaskRepository: FluxTaskRepositoryImpl) {

    fun getFluxTasks(): List<FluxTask> = fluxTaskRepository.getFluxTasks()

    fun onChangeTaskChecked(id: Long, checked: Boolean): List<FluxTask> =
        fluxTaskRepository.onChangeTaskChecked(id, checked)

    fun removeTask(id: Long): List<FluxTask> = fluxTaskRepository.removeTask(id)
}

class FluxTaskRepositoryImpl (
    private val fluxTaskDTOMapper: FluxTaskDTOMapper
) : FluxTaskRepository {

    private var tasks = MutableList(30) { i -> FluxTaskDTO(i.toLong(), "Task # $i") }

    override fun getFluxTasks(): List<FluxTask> = tasks.toList().map { fluxTaskDTOMapper.map(it) }

    override fun removeTask(id: Long): List<FluxTask> =
        if (tasks.removeIf { it.id == id }) tasks.map { fluxTaskDTOMapper.map(it) }
        else throw IllegalStateException("Couldn't remove task!")

    override fun onChangeTaskChecked(id: Long, checked: Boolean): List<FluxTask> =
        tasks.map { task -> if (task.id == id) task.copy(checked = checked) else task }
            .apply { tasks = this.toMutableList() }.map { fluxTaskDTOMapper.map(it) }
}

class FluxTaskDTOMapper {
    fun map(fluxTaskDto: FluxTaskDTO) = with(fluxTaskDto) { FluxTask(id, label, checked) }
}

data class FluxTaskDTO(val id: Long, val label: String, val checked: Boolean = false)

