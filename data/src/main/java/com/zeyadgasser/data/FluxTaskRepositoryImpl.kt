package com.zeyadgasser.data

import com.zeyadgasser.domainPure.FluxTask
import com.zeyadgasser.domainPure.FluxTaskRepository
import javax.inject.Inject

class FluxTaskRepositoryImpl @Inject constructor(private val fluxTaskAPI: FluxTaskAPI) :
    FluxTaskRepository {

    override fun getFluxTasks(): List<FluxTask> = fluxTaskAPI.getFluxTasks().map { it.map() }

    override fun removeTask(id: Long): List<FluxTask> = fluxTaskAPI.removeTask(id).map { it.map() }

    override fun onChangeTaskChecked(id: Long, checked: Boolean): List<FluxTask> =
        fluxTaskAPI.onChangeTaskChecked(id, checked).map { it.map() }
}
