package com.zeyadgasser.data

import com.zeyadgasser.domainPure.FluxTask
import com.zeyadgasser.domainPure.FluxTaskRepository
import javax.inject.Inject

class FluxTaskRepositoryImpl @Inject constructor(
    private val fluxTaskAPI: FluxTaskAPI,
    private val fluxTaskDTOMapper: FluxTaskDTOMapper,
) : FluxTaskRepository {

    override fun getFluxTasks(): List<FluxTask> =
        fluxTaskAPI.getFluxTasks().map { fluxTaskDTOMapper.map(it) }

    override fun removeTask(id: Long): List<FluxTask> =
        fluxTaskAPI.removeTask(id).map { fluxTaskDTOMapper.map(it) }

    override fun onChangeTaskChecked(id: Long, checked: Boolean): List<FluxTask> =
        fluxTaskAPI.onChangeTaskChecked(id, checked).map { fluxTaskDTOMapper.map(it) }
}
