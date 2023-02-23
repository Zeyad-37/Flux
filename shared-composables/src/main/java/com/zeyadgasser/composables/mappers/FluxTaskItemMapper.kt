package com.zeyadgasser.composables.mappers

import com.zeyadgasser.composables.presentation_models.FluxTaskItem
import com.zeyadgasser.domain_pure.FluxTask

class FluxTaskItemMapper {
    fun map(fluxTask: FluxTask) = with(fluxTask) { FluxTaskItem(id, label, checked) }
}
