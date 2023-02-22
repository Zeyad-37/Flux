package com.zeyadgasser.domain

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {
    @Provides
    @ViewModelScoped
    fun provideGetRandomColorIdUseCase(): GetRandomColorIdUseCase = GetRandomColorIdUseCase()

    @Provides
    @ViewModelScoped
    fun provideFluxTaskUseCases(): FluxTaskUseCases = FluxTaskUseCases()
}