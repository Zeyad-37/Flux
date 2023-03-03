package com.zeyadgasser.data

import com.zeyadgasser.domainPure.FluxTask

class FluxTaskDTOMapper {
    fun map(fluxTaskDto: FluxTaskDTO) = with(fluxTaskDto) { FluxTask(id, label, checked) }
}
