package veto.call.blocker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import veto.call.blocker.data.repository.VetoRepositoryImpl
import veto.call.blocker.domain.repository.VetoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVetoRepository(
        vetoRepositoryImpl: VetoRepositoryImpl
    ): VetoRepository
}
