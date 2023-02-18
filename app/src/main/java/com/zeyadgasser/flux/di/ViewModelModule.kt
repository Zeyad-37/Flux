package com.zeyadgasser.flux.di

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.core.ARG_STATE
import com.zeyadgasser.flux.mvi.MVIInputHandler
import com.zeyadgasser.flux.mvi.MVIReducer
import com.zeyadgasser.flux.mvi.MVIState
import com.zeyadgasser.flux.mvvm.MVVMInputHandler
import com.zeyadgasser.flux.mvvm.MVVMState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import com.zeyadgasser.flux.mvi.InitialState as MVIInitialState
import com.zeyadgasser.flux.mvvm.InitialState as MVVMInitialState

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {
    @Provides
    @ViewModelScoped
    fun provideMVIInitialState(savedStateHandle: SavedStateHandle): MVIState =
        savedStateHandle[ARG_STATE] ?: MVIInitialState

    @Provides
    @ViewModelScoped
    fun provideMVIInputHandler(): MVIInputHandler = MVIInputHandler()

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
    fun provideMVVMInputHandler(): MVVMInputHandler = MVVMInputHandler()
}
