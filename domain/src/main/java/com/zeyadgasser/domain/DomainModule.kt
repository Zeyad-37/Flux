package com.zeyadgasser.domain

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {
    @Provides
    @ViewModelScoped
    fun provideGetRandomColorIdUseCase() = GetRandomColorIdUseCase

    @Provides
    @ViewModelScoped
    fun provideFluxTaskUseCases(fluxTaskRepository: FluxTaskRepository): FluxTaskUseCases =
        FluxTaskUseCases(fluxTaskRepository)
}
