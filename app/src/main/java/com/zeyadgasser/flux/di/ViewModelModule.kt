package com.zeyadgasser.flux.di

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.core.ARG_STATE
import com.zeyadgasser.flux.screens.FluxTaskUseCases
import com.zeyadgasser.flux.screens.GetRandomColorIdUseCase
import com.zeyadgasser.flux.screens.mvi.MVIInputHandler
import com.zeyadgasser.flux.screens.mvi.MVIReducer
import com.zeyadgasser.flux.screens.mvi.MVIState
import com.zeyadgasser.flux.screens.mvvm.MVVMInputHandler
import com.zeyadgasser.flux.screens.mvvm.MVVMState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import com.zeyadgasser.flux.screens.mvi.InitialState as MVIInitialState
import com.zeyadgasser.flux.screens.mvvm.InitialState as MVVMInitialState

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {
    @Provides
    @ViewModelScoped
    fun provideMVIInitialState(savedStateHandle: SavedStateHandle): MVIState =
        savedStateHandle[ARG_STATE] ?: MVIInitialState

    @Provides
    @ViewModelScoped
    fun provideGetRandomColorIdUseCase(): GetRandomColorIdUseCase = GetRandomColorIdUseCase()

    @Provides
    @ViewModelScoped
    fun provideFluxTaskUseCases(): FluxTaskUseCases = FluxTaskUseCases()

    @Provides
    @ViewModelScoped
    fun provideMVIInputHandler(
        getRandomColorIdUseCase: GetRandomColorIdUseCase,
        fluxTaskUseCases: FluxTaskUseCases
    ): MVIInputHandler = MVIInputHandler(getRandomColorIdUseCase, fluxTaskUseCases)

    @Provides
    @ViewModelScoped
    fun provideMVIReducer(): MVIReducer = MVIReducer()

    @Provides
    @ViewModelScoped
    fun provideIOCoroutineDispatcher(): CoroutineDispatcher = IO

    @Provides
    @ViewModelScoped
    fun provideMVVMInitialState(savedStateHandle: SavedStateHandle): MVVMState =
        savedStateHandle[ARG_STATE] ?: MVVMInitialState

    @Provides
    @ViewModelScoped
    fun provideMVVMInputHandler(
        getRandomColorIdUseCase: GetRandomColorIdUseCase,
        fluxTaskUseCases: FluxTaskUseCases
    ): MVVMInputHandler = MVVMInputHandler(getRandomColorIdUseCase, fluxTaskUseCases)
}
