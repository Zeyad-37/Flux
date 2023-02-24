package com.zeyadgasser.domain

import javax.inject.Inject

class FluxTaskUseCases @Inject constructor(private val fluxTaskRepository: FluxTaskRepository) {

    fun getFluxTasks(): List<FluxTask> = fluxTaskRepository.getFluxTasks()

    fun onChangeTaskChecked(id: Long, checked: Boolean): List<FluxTask> =
        fluxTaskRepository.onChangeTaskChecked(id, checked)

    fun removeTask(id: Long): List<FluxTask> = fluxTaskRepository.removeTask(id)
}
