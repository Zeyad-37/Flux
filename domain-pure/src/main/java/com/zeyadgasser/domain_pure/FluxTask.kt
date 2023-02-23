package com.zeyadgasser.domain_pure

data class FluxTask(
    override val id: Long,
    val label: String,
    override var checked: Boolean = false
) : CheckableItem
