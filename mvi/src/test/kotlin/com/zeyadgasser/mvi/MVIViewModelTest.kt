package com.zeyadgasser.mvi

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.composables.presentationModels.FluxTaskItem
import com.zeyadgasser.core.api.EmptyResult
import com.zeyadgasser.core.api.Error
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
        mviViewModel.handleInputs(ChangeBackgroundInput, initialState).testOutcomeFlow {
            assertEquals(ChangeBackgroundResult(
                getRandomColorIdUseCase.getRandomColorId(),
                fluxTaskUseCases.getFluxTasks().map { FluxTaskItem(it) }
            ), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun handleInputsShowDialogInput() = runTest {
        mviViewModel.handleInputs(ShowDialogInput, initialState).testOutcomeFlow {
            assertEquals(ShowDialogEffect, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun handleInputsErrorInput() = runTest {
        mviViewModel.handleInputs(ErrorInput, initialState).testOutcomeFlow {
            assertEquals(ErrorResult("Error"), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun handleInputsUncaughtErrorInput() = runTest {
        mviViewModel.handleInputs(UncaughtErrorInput, initialState).testOutcomeFlow {
            val error = awaitItem()
            assertTrue(error is Error)
            assertEquals("UncaughtError", (error as Error).message)
            awaitComplete()
        }
    }

    @Test
    fun handleInputsNavBackInput() = runTest {
        mviViewModel.handleInputs(NavBackInput, initialState).testOutcomeFlow {
            assertEquals(NavBackEffect, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun handleInputsDoNothing() = runTest {
        mviViewModel.handleInputs(DoNothing, initialState).testOutcomeFlow {
            assertEquals(EmptyResult, awaitItem())
            awaitComplete()
        }
    }
}
