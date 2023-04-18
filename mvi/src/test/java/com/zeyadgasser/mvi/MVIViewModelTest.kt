package com.zeyadgasser.mvi

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.EmptyFluxOutcome
import com.zeyadgasser.core.FluxError
import com.zeyadgasser.core.FluxViewModel
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
    fun handleInputsErrorInput() = runTest {
        mviViewModel.handleInputs(ErrorInput, initialState).test {
            assertEquals(ErrorResult("Error").toResultOutcome() as FluxViewModel.FluxResult<MVIResult>, awaitItem())
            awaitComplete()
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
    fun handleInputsNavBackInput() = runTest {
        mviViewModel.handleInputs(NavBackInput, initialState).test {
            assertEquals(NavBackEffect.toEffectOutcome() as FluxViewModel.FluxEffect<MVIEffect>, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun handleInputsDoNothing() = runTest {
        mviViewModel.handleInputs(DoNothing, initialState).test {
            assertEquals(EmptyFluxOutcome, awaitItem())
            awaitComplete()
        }
    }
}
