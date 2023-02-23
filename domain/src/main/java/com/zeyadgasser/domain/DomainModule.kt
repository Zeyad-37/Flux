package com.zeyadgasser.domain

import com.zeyadgasser.domain_pure.*
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

//    @Provides
//    @ViewModelScoped
//    fun provideFluxTaskUseCases(fluxTaskRepository: FluxTaskRepository): FluxTaskUseCases =
//        FluxTaskUseCases(fluxTaskRepository)

    @Provides
    @ViewModelScoped
    fun provideFluxTaskUseCases(): FluxTaskUseCases =
        FluxTaskUseCases(FluxTaskRepositoryImpl(FluxTaskDTOMapper()))
}
