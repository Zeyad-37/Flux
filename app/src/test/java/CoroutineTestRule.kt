package com.zeyadgasser.flux

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace
import org.junit.jupiter.api.extension.ExtensionContext.Store

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineTestExtension : BeforeAllCallback, BeforeEachCallback, Extension {

    private val testDispatcher = StandardTestDispatcher()

    override fun beforeAll(context: ExtensionContext) {
        getStore(context).put("testDispatcher", testDispatcher)
        Dispatchers.setMain(testDispatcher)
    }

    override fun beforeEach(context: ExtensionContext) =
        Dispatchers.setMain(
            getStore(context).get("testDispatcher", StandardTestDispatcher()::class.java)
        )

    private fun getStore(context: ExtensionContext): Store =
        context.getStore(Namespace.create(javaClass))
}
