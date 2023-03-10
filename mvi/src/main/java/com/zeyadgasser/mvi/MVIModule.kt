package com.zeyadgasser.mvi

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
object MVIModule {
    @Provides
    @ViewModelScoped
    fun provideMVIInitialState(savedStateHandle: SavedStateHandle): MVIState =
        savedStateHandle[ARG_STATE] ?: InitialState

    @Provides
    @ViewModelScoped
    fun provideMVIReducer(): MVIReducer = MVIReducer()
}
