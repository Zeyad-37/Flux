package com.zeyadgasser.domainPure

interface FluxTaskRepository {
    fun getFluxTasks(): List<FluxTask>

    fun removeTask(id: Long): List<FluxTask>

    fun onChangeTaskChecked(id: Long, checked: Boolean): List<FluxTask>
}
