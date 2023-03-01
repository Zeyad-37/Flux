package com.zeyadgasser.mvvm

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.zeyadgasser.core.Output
import com.zeyadgasser.core.toStateOutcomeFlow
import com.zeyadgasser.core.toEffectOutcomeFlow
import com.zeyadgasser.core.toErrorOutcomeFlow
import com.zeyadgasser.core.Progress
import com.zeyadgasser.core.InputStrategy.THROTTLE
import com.zeyadgasser.test_base.CoroutineTestExtension
import com.zeyadgasser.test_base.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.Error

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutineTestExtension::class)
class MVVMViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mviViewModel: MVVMViewModel
    private val initialState: MVVMState = InitialState
    private val inputHandler: MVVMInputHandler = mock()

    @BeforeEach
    fun before() {
        mviViewModel = MVVMViewModel(
            initialState, inputHandler, SavedStateHandle(), mainDispatcherRule.testDispatcher
        )
    }

    @Test
    fun changeBackground() = runTest {
        val input = ChangeBackgroundInput
        whenever(inputHandler.handleInputs(any<ChangeBackgroundInput>(), any()))
            .thenReturn(ColorBackgroundState(anyLong(), anyList()).toStateOutcomeFlow())
        mviViewModel.observe().test {
            mviViewModel.process(input, THROTTLE)
            assertEquals(initialState, awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            assertTrue(awaitItem() is ColorBackgroundState)
            assertEquals(Progress(false, input), awaitItem())
        }
    }

    @Test
    fun showDialogInput() = runTest {
        val input = ShowDialogInput
        whenever(inputHandler.handleInputs(input, initialState))
            .thenReturn(ShowDialogEffect.toEffectOutcomeFlow())
        mviViewModel.observe().test {
            mviViewModel.process(input)
            assertEquals(initialState, awaitItem())
            assertEquals(ShowDialogEffect, awaitItem())
            assertEquals(Progress(false, input), awaitItem())
        }
    }

    @Test
    fun errorInput() = runTest {
        val input = ErrorInput
        whenever(inputHandler.handleInputs(any<ErrorInput>(), any()))
            .thenReturn(ErrorState("Error").toStateOutcomeFlow())
        mviViewModel.observe().test {
            mviViewModel.process(input)
            assertEquals(initialState, awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            val error: Output = awaitItem()
            assertTrue(error is ErrorState)
            assertEquals("Error", (error as ErrorState).message)
            assertEquals(Progress(false, input), awaitItem())
        }
    }

    @Test
    fun uncaughtErrorInput() = runTest {
        val input = UncaughtErrorInput
        whenever(inputHandler.handleInputs(input, initialState))
            .thenReturn(IllegalStateException("UncaughtError").toErrorOutcomeFlow())
        mviViewModel.observe().test {
            mviViewModel.process(input)
            assertEquals(initialState, awaitItem())
            val error: Output = awaitItem()
            assertTrue(error is Error)
            assertEquals("UncaughtError", (error as Error).message)
            assertEquals(Progress(false, input), awaitItem())
        }
    }

    @Test
    fun navBackInput() = runTest {
        val input = NavBackInput
        whenever(inputHandler.handleInputs(input, initialState))
            .thenReturn(NavBackEffect.toEffectOutcomeFlow())
        mviViewModel.observe().test {
            mviViewModel.process(input)
            assertEquals(initialState, awaitItem())
            assertEquals(NavBackEffect, awaitItem())
            assertEquals(Progress(false, input), awaitItem())
        }
    }
}
