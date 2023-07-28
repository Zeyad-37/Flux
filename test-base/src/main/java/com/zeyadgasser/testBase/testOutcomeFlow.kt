package com.zeyadgasser.testBase

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.zeyadgasser.core.Outcome
import com.zeyadgasser.core.api.AsyncOutcomeFlow
import kotlinx.coroutines.flow.Flow

suspend fun Flow<Outcome>.testOutcomeFlow(assertAndVerify: suspend ReceiveTurbine<Outcome>.() -> Unit): Unit =
    if (this@testOutcomeFlow is AsyncOutcomeFlow) flow.test { assertAndVerify.invoke(this) }
    else test { assertAndVerify.invoke(this) }
