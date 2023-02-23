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
    fun provideFluxTaskDTOMapper(): FluxTaskDTOMapper = FluxTaskDTOMapper()

    @Provides
    @ViewModelScoped
    fun provideFluxTaskRepositoryImpl(fluxTaskDTOMapper: FluxTaskDTOMapper): FluxTaskRepositoryImpl =
        FluxTaskRepositoryImpl(fluxTaskDTOMapper)
}

//@Module
//@InstallIn(ViewModelComponent::class)
//abstract class BindingModule {
//    @Binds
//    abstract fun bindFluxTaskRepository(fluxTaskRepository: FluxTaskRepositoryImpl): FluxTaskRepository
//}
