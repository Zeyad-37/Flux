package com.zeyadgasser.mvi

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.zeyadgasser.core.EmptyInput
import com.zeyadgasser.core.InputStrategy.THROTTLE
import com.zeyadgasser.core.Progress
import com.zeyadgasser.core.toEffectOutcomeFlow
import com.zeyadgasser.core.toResultOutcomeFlow
import com.zeyadgasser.core.toErrorOutcomeFlow
import com.zeyadgasser.core.Output
import com.zeyadgasser.testBase.CoroutineTestExtension
import com.zeyadgasser.testBase.MainDispatcherRule
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutineTestExtension::class)
class MVIViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val initialState: MVIState = InitialState
    private val inputHandler: MVIInputHandler = mock()
    private val reducer: MVIReducer = MVIReducer()

    private lateinit var mviViewModel: MVIViewModel

    @BeforeEach
    fun before() {
        mviViewModel = MVIViewModel(
            initialState,
            inputHandler,
            reducer,
            SavedStateHandle(),
            mainDispatcherRule.testDispatcher
        )
    }

    @Test
    fun changeBackground() = runTest {
        val input = ChangeBackgroundInput
        whenever(inputHandler.handleInputs(input, initialState))
            .thenReturn(ChangeBackgroundResult(anyLong(), anyList()).toResultOutcomeFlow())
        mviViewModel.observe().test {
            mviViewModel.process(input, THROTTLE)
            assertEquals(initialState, awaitItem())
            assertEquals(Progress(false, EmptyInput), awaitItem())
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
            assertEquals(Progress(false, EmptyInput), awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            assertEquals(ShowDialogEffect, awaitItem())
            assertEquals(Progress(false, input), awaitItem())
        }
    }

    @Test
    fun errorInput() = runTest {
        val input = ErrorInput
        whenever(inputHandler.handleInputs(input, initialState))
            .thenReturn(ErrorResult("Error").toResultOutcomeFlow())
        mviViewModel.observe().test {
            mviViewModel.process(input)
            assertEquals(initialState, awaitItem())
            assertEquals(Progress(false, EmptyInput), awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            assertEquals(ErrorState("Error"), awaitItem())
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
            assertEquals(Progress(false, EmptyInput), awaitItem())
            assertEquals(Progress(true, input), awaitItem())
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
            assertEquals(Progress(false, EmptyInput), awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            assertEquals(NavBackEffect, awaitItem())
            assertEquals(Progress(false, input), awaitItem())
        }
    }
}
