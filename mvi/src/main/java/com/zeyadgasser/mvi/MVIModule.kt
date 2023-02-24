package com.zeyadgasser.mvi

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.composables.mappers.FluxTaskItemMapper
import com.zeyadgasser.core.ARG_STATE
import com.zeyadgasser.domain_pure.FluxTaskUseCases
import com.zeyadgasser.domain_pure.GetRandomColorIdUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object MVIModule {
    @Provides
    @ViewModelScoped
    fun provideMVIInitialState(savedStateHandle: SavedStateHandle): MVIState =
        savedStateHandle[ARG_STATE] ?: InitialState

    @Provides
    @ViewModelScoped
    fun provideMVIInputHandler(
        getRandomColorIdUseCase: GetRandomColorIdUseCase,
        fluxTaskUseCases: FluxTaskUseCases,
        fluxTaskItemMapper: FluxTaskItemMapper,
    ) = MVIInputHandler(getRandomColorIdUseCase, fluxTaskUseCases, fluxTaskItemMapper)

    @Provides
    @ViewModelScoped
    fun provideMVIReducer(): MVIReducer = MVIReducer()
}
