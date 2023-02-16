package com.zeyadgasser.flux.mvi

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.zeyadgasser.core.*
import com.zeyadgasser.flux.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MVIViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mviViewModel: MVIViewModel

    @Before
    fun before() {
        mviViewModel = MVIViewModel(
            InitialState,
            MVIInputHandler(),
            MVIReducer(),
            SavedStateHandle(),
            mainDispatcherRule.testDispatcher
        )
    }

    @Test
    fun changeBackground() = runTest {
        mviViewModel.observe().test {
            val input = ChangeBackgroundInput()
            mviViewModel.process(input, InputStrategy.THROTTLE)
            assertEquals(InitialState, awaitItem())
            assertEquals(Progress(false, EmptyInput), awaitItem())
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
            assertEquals(Progress(false, EmptyInput), awaitItem())
            assertEquals(Progress(true, input), awaitItem())
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
            assertEquals(Progress(false, EmptyInput), awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            assertEquals(ErrorState("Error"), awaitItem())
            assertEquals(Progress(false, input), awaitItem())
        }
    }

    @Test
    fun uncaughtErrorInput() = runTest {
        mviViewModel.observe().test {
            val input = UncaughtErrorInput
            mviViewModel.process(input)
            assertEquals(InitialState, awaitItem())
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
        mviViewModel.observe().test {
            val input = NavBackInput
            mviViewModel.process(input)
            assertEquals(InitialState, awaitItem())
            assertEquals(Progress(false, EmptyInput), awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            assertEquals(NavBackEffect, awaitItem())
            assertEquals(Progress(false, input), awaitItem())
        }
    }
}
