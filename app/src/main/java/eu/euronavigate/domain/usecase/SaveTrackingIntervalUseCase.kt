package eu.euronavigate.domain.usecase

import eu.euronavigate.data.local.SettingsDataStore
import javax.inject.Inject

class SaveTrackingIntervalUseCase @Inject constructor(
	private val settingsDataStore: SettingsDataStore
) {
	suspend operator fun invoke(interval: Long) {
		settingsDataStore.saveInterval(interval)
	}
}
