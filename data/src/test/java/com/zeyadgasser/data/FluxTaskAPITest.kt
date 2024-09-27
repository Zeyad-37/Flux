package com.zeyadgasser.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FluxTaskAPITest {

    private lateinit var fluxTaskAPI: FluxTaskAPI
    private val list: MutableList<FluxTaskDTO>
        get() = MutableList(37) { i -> FluxTaskDTO(i.toLong(), "Task # $i") }

    @BeforeEach
    fun setUp() {
        fluxTaskAPI = FluxTaskAPI()
    }

    @Test
    fun getFluxTasks() {
        val expected = list
        val actual = fluxTaskAPI.getFluxTasks()
        assertEquals(expected, actual)
    }

    @Test
    fun onChangeTaskChecked() {
        val expected =
            list.mapIndexed { index, fluxTaskDTO -> if (index == 5) fluxTaskDTO.copy(checked = true) else fluxTaskDTO }
        val actual = fluxTaskAPI.onChangeTaskChecked(5, true)
        assertEquals(expected, actual)
    }

    @Test
    fun removeTask() {
        val testList = list
        testList.removeAt(1)
        val actual = fluxTaskAPI.removeTask(1)
        assertEquals(testList, actual)
    }
}
