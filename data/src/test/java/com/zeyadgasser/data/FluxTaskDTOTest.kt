package com.zeyadgasser.data

import com.zeyadgasser.domainPure.FluxTask
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FluxTaskDTOTest {

    private val fluxTaskDTO: FluxTaskDTO = FluxTaskDTO(1, "Task 1", false)

    @Test
    fun mapToFluxTask() {
        val expected = FluxTask(1, "Task 1", false)
        val actual = fluxTaskDTO.map()
        assertEquals(expected, actual)
    }
}
