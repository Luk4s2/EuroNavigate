package eu.euronavigate.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.euronavigate.data.repository.ILocationRepository
import eu.euronavigate.data.repository.LocationRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

	@Binds
	@Singleton
	abstract fun bindLocationRepository(
		impl: LocationRepositoryImpl
	): ILocationRepository
}
