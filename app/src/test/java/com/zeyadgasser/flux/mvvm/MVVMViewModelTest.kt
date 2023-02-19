package com.zeyadgasser.flux.mvvm

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.zeyadgasser.core.*
import com.zeyadgasser.flux.CoroutineTestExtension
import com.zeyadgasser.flux.MainDispatcherRule
import com.zeyadgasser.flux.screens.main.mvvm.*
import com.zeyadgasser.flux.screens.mvvm.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.spy

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutineTestExtension::class)
class MVVMViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mviViewModel: MVVMViewModel
    private val mvvmInputHandler: MVVMInputHandler = spy()

    @BeforeEach
    fun before() {
        mviViewModel = MVVMViewModel(
            InitialState, mvvmInputHandler, SavedStateHandle(), mainDispatcherRule.testDispatcher
        )
    }
    @Test
    fun changeBackground() = runTest {
        mviViewModel.observe().test {
            val input = ChangeBackgroundInput
            mviViewModel.process(input, InputStrategy.THROTTLE)
            assertEquals(InitialState, awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            assertTrue(awaitItem() is ColorBackgroundState)
            assertEquals(Progress(false, input), awaitItem())
        }
    }

    @Test
    fun showDialogInput() = runTest {
        mviViewModel.observe().test {
            val input = ShowDialogInput
            mviViewModel.process(input)
            assertEquals(InitialState, awaitItem())
            assertEquals(ShowDialogEffect, awaitItem())
            assertEquals(Progress(false, input), awaitItem())
        }
    }

    @Test
    fun errorInput() = runTest {
        mviViewModel.observe().test {
            val input = ErrorInput
            mviViewModel.process(input)
            assertEquals(InitialState, awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            val error: Output = awaitItem()
            assertTrue(error is ErrorState)
            assertEquals("Error", (error as ErrorState).message)
            assertEquals(Progress(false, input), awaitItem())
        }
    }

    @Test
    fun uncaughtErrorInput() = runTest {
        mviViewModel.observe().test {
            val input = UncaughtErrorInput
            mviViewModel.process(input)
            assertEquals(InitialState, awaitItem())
            val error: Output = awaitItem()
            assertTrue(error is Error)
            assertEquals("UncaughtError", (error as Error).message)
            assertEquals(Progress(false, input), awaitItem())
        }
    }

    @Test
    fun navBackInput() = runTest {
        mviViewModel.observe().test {
            val input = NavBackInput
            mviViewModel.process(input)
            assertEquals(InitialState, awaitItem())
            assertEquals(NavBackEffect, awaitItem())
            assertEquals(Progress(false, input), awaitItem())
        }
    }
}
