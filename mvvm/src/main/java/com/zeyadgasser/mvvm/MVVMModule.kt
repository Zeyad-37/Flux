package com.zeyadgasser.mvvm

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.core.ARG_STATE
import com.zeyadgasser.domainPure.FluxTaskUseCases
import com.zeyadgasser.domainPure.GetRandomColorIdUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object MVVMModule {
    @Provides
    @ViewModelScoped
    fun provideMVVMInitialState(savedStateHandle: SavedStateHandle): MVVMState =
        savedStateHandle[ARG_STATE] ?: InitialState

    @Provides
    @ViewModelScoped
    fun provideMVIInputHandler(
        getRandomColorIdUseCase: GetRandomColorIdUseCase,
        fluxTaskUseCases: FluxTaskUseCases,
    ) = MVVMInputHandler(getRandomColorIdUseCase, fluxTaskUseCases)
}
