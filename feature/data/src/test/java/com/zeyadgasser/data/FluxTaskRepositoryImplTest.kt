package com.zeyadgasser.data

import com.zeyadgasser.domainPure.FluxTask
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class FluxTaskRepositoryImplTest {

    private lateinit var fluxTaskRepository: FluxTaskRepositoryImpl
    private val fluxTaskAPI: FluxTaskAPI = mock()
    
    @BeforeEach
    fun setUp() {
        fluxTaskRepository = FluxTaskRepositoryImpl(fluxTaskAPI)
    }

    @Test
    fun getFluxTasks() {
        val expected = listOf(FluxTask(1, "Task 1"))
        whenever(fluxTaskAPI.getFluxTasks()).thenReturn(listOf(FluxTaskDTO(1, "Task 1")))
        val actual = fluxTaskRepository.getFluxTasks()
        assertEquals(expected, actual)
    }

    @Test
    fun onChangeTaskChecked() {
        val expected = listOf(FluxTask(1, "Task 1", true))
        whenever(fluxTaskAPI.onChangeTaskChecked(1, true)).thenReturn(listOf(FluxTaskDTO(1, "Task 1", true)))
        val actual = fluxTaskRepository.onChangeTaskChecked(1, true)
        assertEquals(expected, actual)
    }

    @Test
    fun removeTask() {
        whenever(fluxTaskAPI.removeTask(1)).thenReturn(emptyList())
        val actual = fluxTaskRepository.removeTask(1)
        assertEquals(emptyList<FluxTask>(), actual)
    }
}
