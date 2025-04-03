package eu.euronavigate.domain.usecase

import eu.euronavigate.data.local.SettingsDataStore
import javax.inject.Inject

class GetSavedIntervalUseCase @Inject constructor(
	private val settingsDataStore: SettingsDataStore
) {
	suspend operator fun invoke(): Long {
		return settingsDataStore.getInterval()
	}
}
