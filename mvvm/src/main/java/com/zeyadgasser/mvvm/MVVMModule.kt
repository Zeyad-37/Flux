package com.zeyadgasser.mvvm

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.core.FluxViewModel
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
        savedStateHandle[FluxViewModel.ARG_STATE_KEY] ?: InitialState
}
