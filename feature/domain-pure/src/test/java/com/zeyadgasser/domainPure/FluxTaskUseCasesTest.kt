package com.zeyadgasser.domainPure

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class FluxTaskUseCasesTest {

    private lateinit var fluxTaskUseCases: FluxTaskUseCases
    private val fluxTaskRepository: FluxTaskRepository = mock()

    @BeforeEach
    fun setUp() {
        fluxTaskUseCases = FluxTaskUseCases(fluxTaskRepository)
    }

    @Test
    fun getFluxTasks() {
        val expected = listOf(FluxTask(1, "Task 1"))
        whenever(fluxTaskRepository.getFluxTasks()).thenReturn(expected)
        val actual = fluxTaskUseCases.getFluxTasks()
        assertEquals(expected, actual)
    }

    @Test
    fun onChangeTaskChecked() {
        val expected = listOf(FluxTask(1, "Task 1", true))
        whenever(fluxTaskRepository.onChangeTaskChecked(1, true)).thenReturn(expected)
        val actual = fluxTaskUseCases.onChangeTaskChecked(1, true)
        assertEquals(expected, actual)
    }

    @Test
    fun removeTask() {
        whenever(fluxTaskRepository.removeTask(1)).thenReturn(emptyList())
        val actual = fluxTaskUseCases.removeTask(1)
        assertEquals(emptyList<FluxTask>(), actual)
    }
}
