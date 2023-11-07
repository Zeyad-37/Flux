package com.zeyadgasser.testBase

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.zeyadgasser.core.api.Result
import com.zeyadgasser.core.api.AsyncResultFlow
import kotlinx.coroutines.flow.Flow

suspend fun Flow<Result>.testOutcomeFlow(assertAndVerify: suspend ReceiveTurbine<Result>.() -> Unit): Unit =
    if (this@testOutcomeFlow is AsyncResultFlow) flow.test { assertAndVerify.invoke(this) }
    else test { assertAndVerify.invoke(this) }
