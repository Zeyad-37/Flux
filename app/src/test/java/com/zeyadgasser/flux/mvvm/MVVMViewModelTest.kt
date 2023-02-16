package com.zeyadgasser.flux.mvvm

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
import org.mockito.kotlin.spy

@OptIn(ExperimentalCoroutinesApi::class)
class MVVMViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mviViewModel: MVVMViewModel
    private val mvvmInputHandler: MVVMInputHandler = spy()

    @Before
    fun before() {
        mviViewModel = MVVMViewModel(
            InitialState, mvvmInputHandler, SavedStateHandle(), mainDispatcherRule.testDispatcher
        )
    }

//    @Test
//    fun changeBackground() = runTest {
//        val input = ChangeBackgroundInput()
//        whenever(mvvmInputHandler.handleInputs(input, InitialState))
//            .thenReturn(
//                ColorBackgroundState(any())
//                    .toStateOutcomeFlow().onStart { delay(1000) }
//            )
//        mviViewModel.observe().test {
//            mviViewModel.process(input, InputStrategy.THROTTLE)
//            assertEquals(InitialState, awaitItem())
//            assertEquals(Progress(false, EmptyInput), awaitItem())
//            assertEquals(Progress(true, input), awaitItem())
//            assertTrue(awaitItem() is ColorBackgroundState)
//            assertEquals(Progress(false, input), awaitItem())
//        }
//    }

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
            val error: Output = awaitItem()
            assertTrue(error is Error)
            assertEquals("Test", (error as Error).message)
            assertEquals(Progress(false, input), awaitItem())
        }
    }
}
