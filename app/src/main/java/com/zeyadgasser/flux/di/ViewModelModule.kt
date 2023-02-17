package com.zeyadgasser.flux.di

import androidx.lifecycle.SavedStateHandle
import com.zeyadgasser.core.ARG_STATE
import com.zeyadgasser.flux.mvi.InitialState
import com.zeyadgasser.flux.mvi.MVIInputHandler
import com.zeyadgasser.flux.mvi.MVIReducer
import com.zeyadgasser.flux.mvi.MVIState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {
    @Provides
    @ViewModelScoped
    fun provideInitialState(savedStateHandle: SavedStateHandle): MVIState =
        savedStateHandle[ARG_STATE] ?: InitialState

    @Provides
    @ViewModelScoped
    fun provideMVIInputHandler(): MVIInputHandler = MVIInputHandler()

    @Provides
    @ViewModelScoped
    fun provideMVIReducer(): MVIReducer = MVIReducer()

    @Provides
    @ViewModelScoped
    fun provideIOCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
