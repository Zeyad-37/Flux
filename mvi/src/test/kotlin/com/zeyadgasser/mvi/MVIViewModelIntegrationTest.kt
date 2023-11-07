package com.zeyadgasser.mvi

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.zeyadgasser.core.api.Error
import com.zeyadgasser.core.api.Output
import com.zeyadgasser.core.api.Progress
import com.zeyadgasser.data.FluxTaskAPI
import com.zeyadgasser.data.FluxTaskRepositoryImpl
import com.zeyadgasser.domainPure.FluxTaskRepository
import com.zeyadgasser.domainPure.FluxTaskUseCases
import com.zeyadgasser.domainPure.GetRandomColorIdUseCase
import com.zeyadgasser.testBase.CoroutineTestExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutineTestExtension::class)
class MVIViewModelIntegrationTest {

    private val initialState: MVIState = InitialState
    private val reducer: MVIReducer = MVIReducer()
    private val getRandomColorIdUseCase = GetRandomColorIdUseCase

    private lateinit var fluxTaskAPI: FluxTaskAPI
    private lateinit var fluxTaskRepository: FluxTaskRepository
    private lateinit var fluxTaskUseCases: FluxTaskUseCases
    private lateinit var mviViewModel: MVIViewModel

    @BeforeEach
    fun before() {
        fluxTaskAPI = FluxTaskAPI()
        fluxTaskRepository = FluxTaskRepositoryImpl(fluxTaskAPI)
        fluxTaskUseCases = FluxTaskUseCases(fluxTaskRepository)
        mviViewModel = MVIViewModel(
            getRandomColorIdUseCase,
            fluxTaskUseCases,
            initialState,
            reducer,
            SavedStateHandle(),
        )
    }

    @Test
    fun changeBackground() = runTest {
        val input = ChangeBackgroundInput
        mviViewModel.observe().test {
            mviViewModel.process(input)
            assertEquals(initialState, awaitItem())
            assertEquals(Progress(true, input), awaitItem())
            assertTrue(awaitItem() is ColorBackgroundState)
            assertEquals(Progress(false, input), awaitItem())
            ensureAllEventsConsumed()
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
            ensureAllEventsConsumed()
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
            ensureAllEventsConsumed()
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
            ensureAllEventsConsumed()
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
            ensureAllEventsConsumed()
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
            ensureAllEventsConsumed()
        }
    }
}
