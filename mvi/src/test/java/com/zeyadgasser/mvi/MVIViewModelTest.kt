package com.zeyadgasser.mvi

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.*
import com.zeyadgasser.domainPure.FluxTask
import com.zeyadgasser.domainPure.FluxTaskUseCases
import com.zeyadgasser.domainPure.GetRandomColorIdUseCase
import com.zeyadgasser.domainPure.PURPLE_200
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutineTestExtension::class)
class MVIViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val initialState: MVIState = InitialState
    private val reducer: MVIReducer = MVIReducer()
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase = mock()
    private val fluxTaskUseCases: FluxTaskUseCases = mock()

    private lateinit var mviViewModel: MVIViewModel

    @BeforeEach
    fun before() {
        mviViewModel = MVIViewModel(
            getRandomColorIdUseCase,
            fluxTaskUseCases,
            initialState,
            reducer,
            SavedStateHandle(),
            mainDispatcherRule.testDispatcher
        )
    }

    @Test
    fun handleInputsChangeBackground() = runTest {
        whenever(getRandomColorIdUseCase.getRandomColorId()).thenReturn(PURPLE_200)
        whenever(fluxTaskUseCases.getFluxTasks()).thenReturn(
            List(10) { i -> FluxTask(i.toLong(), "Task # $i") }
        )
        mviViewModel.handleInputs(ChangeBackgroundInput, initialState).test {
            assertEquals(ChangeBackgroundResult(
                getRandomColorIdUseCase.getRandomColorId(),
                fluxTaskUseCases.getFluxTasks().map { FluxTaskItem(it) }
            ).toResultOutcome() as FluxViewModel.FluxResult<MVIResult>, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun changeBackground() = runTest {
        val input = ChangeBackgroundInput
        whenever(getRandomColorIdUseCase.getRandomColorId()).thenReturn(PURPLE_200)
        whenever(fluxTaskUseCases.getFluxTasks()).thenReturn(
            List(10) { i -> FluxTask(i.toLong(), "Task # $i") }
        )
        mviViewModel.observe().test {
            mviViewModel.process(input)
            assertEquals(initialState, awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            assertTrue(awaitItem() is ColorBackgroundState)
            assertEquals(Progress(false, input), awaitItem())
        }
    }

    @Test
    fun showDialogInput() = runTest {
        val input = ShowDialogInput
        mviViewModel.observe().test {
            mviViewModel.process(input)
            assertEquals(initialState, awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            assertEquals(ShowDialogEffect, awaitItem())
            assertEquals(Progress(false, input), awaitItem())
        }
    }

    @Test
    fun handleInputsErrorInput() = runTest {
        mviViewModel.handleInputs(ErrorInput, initialState).test {
            assertEquals(ErrorResult("Error").toResultOutcome() as FluxViewModel.FluxResult<MVIResult>, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun errorInput() = runTest {
        val input = ErrorInput
        mviViewModel.observe().test {
            mviViewModel.process(input)
            assertEquals(initialState, awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            assertEquals(ErrorState("Error"), awaitItem())
            assertEquals(Progress(false, input), awaitItem())
        }
    }

    @Test
    fun handleInputsUncaughtErrorInput() = runTest {
       mviViewModel.handleInputs(UncaughtErrorInput, initialState).test {
            val error = awaitItem()
            assertTrue(error is FluxError)
            assertEquals("UncaughtError", (error as FluxError).error.message)
            awaitComplete()
        }
    }

    @Test
    fun uncaughtErrorInput() = runTest {
        val input = UncaughtErrorInput
        mviViewModel.observe().test {
            mviViewModel.process(input)
            assertEquals(initialState, awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            val error: Output = awaitItem()
            assertTrue(error is Error)
            assertEquals("UncaughtError", (error as Error).message)
            assertEquals(Progress(false, input), awaitItem())
        }
    }

    @Test
    fun handleInputsNavBackInput() = runTest { // TODO
        mviViewModel.handleInputs(NavBackInput, initialState).test {
            assertEquals(ErrorResult("Error").toResultOutcome() as FluxViewModel.FluxResult<MVIResult>, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun navBackInput() = runTest {
        val input = NavBackInput
        mviViewModel.observe().test {
            mviViewModel.process(input)
            assertEquals(initialState, awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            assertEquals(NavBackEffect, awaitItem())
            assertEquals(Progress(false, input), awaitItem())
        }
    }

    @Test
    fun handleInputsDoNothing() = runTest { // TODO
        mviViewModel.handleInputs(DoNothing, initialState).test {
            assertEquals(ErrorResult("Error").toResultOutcome() as FluxViewModel.FluxResult<MVIResult>, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun doNothingInput() = runTest {
        val input = DoNothing
        mviViewModel.observe().test {
            mviViewModel.process(input)
            assertEquals(initialState, awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            assertEquals(Progress(false, input), awaitItem())
        }
    }
}
