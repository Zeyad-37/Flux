package com.zeyadgasser.data

import com.zeyadgasser.domain_pure.FluxTaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class BindingModule {
    @Binds
    abstract fun bindFluxTaskRepository(fluxTaskRepository: FluxTaskRepositoryImpl): FluxTaskRepository
}
