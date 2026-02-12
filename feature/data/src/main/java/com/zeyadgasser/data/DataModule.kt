package com.zeyadgasser.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object DataModule {

    @Provides
    @ViewModelScoped
    fun provideFluxTaskAPI(): FluxTaskAPI = FluxTaskAPI()

    @Provides
    @ViewModelScoped
    fun provideFluxTaskRepositoryImpl(fluxTaskAPI: FluxTaskAPI): FluxTaskRepositoryImpl =
        FluxTaskRepositoryImpl(fluxTaskAPI)
}
