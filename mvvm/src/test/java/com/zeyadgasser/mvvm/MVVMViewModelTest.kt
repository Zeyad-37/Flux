package com.zeyadgasser.mvvm

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.v1.Outcome
import com.zeyadgasser.domainPure.FluxTask
import com.zeyadgasser.domainPure.FluxTaskUseCases
import com.zeyadgasser.domainPure.GetRandomColorIdUseCase
import com.zeyadgasser.domainPure.PURPLE_200
import com.zeyadgasser.testBase.CoroutineTestExtension
import com.zeyadgasser.testBase.testOutcomeFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(CoroutineTestExtension::class)
class MVVMViewModelTest {

    private lateinit var mviViewModel: MVVMViewModel
    private val initialState: MVVMState = InitialState
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase = mock()
    private val fluxTaskUseCases: FluxTaskUseCases = mock()
    private val savedStateHandle: SavedStateHandle = mock()

    @BeforeEach
    fun before() {
        mviViewModel = MVVMViewModel(
            getRandomColorIdUseCase, fluxTaskUseCases, initialState, savedStateHandle
        )
    }

    @Test
    fun changeBackground() = runTest {
        val input = ChangeBackgroundInput
        whenever(getRandomColorIdUseCase.getRandomColorId()).thenReturn(PURPLE_200)
        whenever(fluxTaskUseCases.getFluxTasks()).thenReturn(
            List(10) { i -> FluxTask(i.toLong(), "Task # $i") }
        )
        mviViewModel.handleInputs(input, initialState).testOutcomeFlow {
            assertEquals(ColorBackgroundState(
                PURPLE_200,
                List(10) { i -> FluxTask(i.toLong(), "Task # $i") }.map { FluxTaskItem(it) }
            ).toStateOutcome(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun showDialogInput() = runTest {
        mviViewModel.handleInputs(ShowDialogInput, initialState).testOutcomeFlow {
            assertEquals(ShowDialogEffect.toEffectOutcome(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun errorInput() = runTest {
        val input = ErrorInput
        mviViewModel.handleInputs(input, initialState).test {
            assertEquals(ErrorState("Error").toStateOutcome(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun uncaughtErrorInput() = runTest {
        val input = UncaughtErrorInput
        mviViewModel.handleInputs(input, initialState).testOutcomeFlow {
            val error = awaitItem()
            assertTrue(error is Outcome.ErrorOutcome)
            assertEquals("UncaughtError", (error as Outcome.ErrorOutcome).error.message)
            awaitComplete()
        }
    }

    @Test
    fun navBackInput() = runTest {
        val input = NavBackInput
        mviViewModel.handleInputs(input, initialState).testOutcomeFlow {
            assertEquals(NavBackEffect.toEffectOutcome(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun doNothingInput() = runTest {
        val input = DoNothing
        mviViewModel.handleInputs(input, initialState).testOutcomeFlow {
            assertEquals(Outcome.EmptyOutcome, awaitItem())
            awaitComplete()
        }
    }
}
