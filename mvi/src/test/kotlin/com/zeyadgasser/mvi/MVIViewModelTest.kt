package com.zeyadgasser.mvi

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.FluxViewModel.EffectOutcome
import com.zeyadgasser.core.FluxViewModel.ResultOutcome
import com.zeyadgasser.core.Outcome
import com.zeyadgasser.domainPure.FluxTask
import com.zeyadgasser.domainPure.FluxTaskUseCases
import com.zeyadgasser.domainPure.GetRandomColorIdUseCase
import com.zeyadgasser.domainPure.PURPLE_200
import com.zeyadgasser.testBase.CoroutineTestExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(CoroutineTestExtension::class)
class MVIViewModelTest {

    private val initialState: MVIState = InitialState
    private val reducer: MVIReducer = MVIReducer()
    private val getRandomColorIdUseCase: GetRandomColorIdUseCase = mock()
    private val fluxTaskUseCases: FluxTaskUseCases = mock()
    private val savedStateHandle: SavedStateHandle = mock()

    private lateinit var mviViewModel: MVIViewModel

    @BeforeEach
    fun before() {
        mviViewModel = MVIViewModel(
            getRandomColorIdUseCase, fluxTaskUseCases, initialState, reducer, savedStateHandle,
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
            ).toResultOutcome() as ResultOutcome<MVIResult>, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun handleInputsErrorInput() = runTest {
        mviViewModel.handleInputs(ErrorInput, initialState).test {
            assertEquals(ErrorResult("Error").toResultOutcome() as ResultOutcome<MVIResult>, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun handleInputsUncaughtErrorInput() = runTest {
        mviViewModel.handleInputs(UncaughtErrorInput, initialState).test {
            val error = awaitItem()
            assertTrue(error is Outcome.ErrorOutcome)
            assertEquals("UncaughtError", (error as Outcome.ErrorOutcome).error.message)
            awaitComplete()
        }
    }

    @Test
    fun handleInputsNavBackInput() = runTest {
        mviViewModel.handleInputs(NavBackInput, initialState).test {
            assertEquals(NavBackEffect.toEffectOutcome() as EffectOutcome<MVIEffect>, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun handleInputsDoNothing() = runTest {
        mviViewModel.handleInputs(DoNothing, initialState).test {
            assertEquals(Outcome.EmptyOutcome, awaitItem())
            awaitComplete()
        }
    }
}
