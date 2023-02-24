package com.zeyadgasser.domain

import com.zeyadgasser.domain_pure.FluxTaskRepository
import com.zeyadgasser.domain_pure.FluxTaskUseCases
import com.zeyadgasser.domain_pure.GetRandomColorIdUseCase
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
