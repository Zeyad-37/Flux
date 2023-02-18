package com.zeyadgasser.flux.main

sealed interface Destination {
    val route: String
}

object Main : Destination {
    override val route = "overview"
}

object MVI : Destination {
    override val route = "accounts"
}

object MVVM : Destination {
    override val route = "bills"
}
