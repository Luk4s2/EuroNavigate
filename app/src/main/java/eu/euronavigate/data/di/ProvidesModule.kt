package eu.euronavigate.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import eu.euronavigate.data.local.SettingsDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProvidesModule {

	@Provides
	@Singleton
	fun provideSettingsDataStore(
		@ApplicationContext context: Context
	): SettingsDataStore {
		return SettingsDataStore(context)
	}
}
