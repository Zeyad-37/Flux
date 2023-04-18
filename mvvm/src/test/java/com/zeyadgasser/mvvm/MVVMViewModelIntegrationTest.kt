package com.zeyadgasser.mvvm

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.zeyadgasser.core.Error
import com.zeyadgasser.core.Output
import com.zeyadgasser.core.Progress
import com.zeyadgasser.data.FluxTaskAPI
import com.zeyadgasser.data.FluxTaskRepositoryImpl
import com.zeyadgasser.domainPure.FluxTaskRepository
import com.zeyadgasser.domainPure.FluxTaskUseCases
import com.zeyadgasser.domainPure.GetRandomColorIdUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutineTestExtension::class)
class MVVMViewModelIntegrationTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mviViewModel: MVVMViewModel
    private lateinit var fluxTaskUseCases: FluxTaskUseCases
    private lateinit var fluxTaskAPI: FluxTaskAPI
    private lateinit var fluxTaskRepository: FluxTaskRepository
    private val initialState: MVVMState = InitialState
    private val getRandomColorIdUseCase = GetRandomColorIdUseCase

    @BeforeEach
    fun before() {
        fluxTaskAPI = FluxTaskAPI()
        fluxTaskRepository = FluxTaskRepositoryImpl(fluxTaskAPI)
        fluxTaskUseCases = FluxTaskUseCases(fluxTaskRepository)
        mviViewModel = MVVMViewModel(
            getRandomColorIdUseCase,
            fluxTaskUseCases,
            initialState,
            SavedStateHandle(),
            mainDispatcherRule.testDispatcher
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
    fun errorInput() = runTest {
        val input = ErrorInput
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
