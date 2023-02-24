package com.zeyadgasser.data

import com.zeyadgasser.domain.FluxTask

class FluxTaskDTOMapper {
    fun map(fluxTaskDto: FluxTaskDTO) = with(fluxTaskDto) { FluxTask(id, label, checked) }
}
