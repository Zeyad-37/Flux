package com.zeyadgasser.composables.mappers

import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.domainPure.FluxTask

class FluxTaskItemMapper {
    fun map(fluxTask: FluxTask): FluxTaskItem = with(fluxTask) { FluxTaskItem(id, label, checked) }
}
