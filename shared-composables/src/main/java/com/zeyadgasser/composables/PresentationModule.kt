package com.zeyadgasser.composables

import com.zeyadgasser.composables.mappers.FluxTaskItemMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object PresentationModule {
    @Provides
    @ViewModelScoped
    fun provideFluxTaskItemMapper() = FluxTaskItemMapper()
}
