package eu.euronavigate.domain.usecase

import eu.euronavigate.data.repository.ILocationRepository
import javax.inject.Inject

class StartTrackingUseCase @Inject constructor(
	val repository: ILocationRepository
) {
	suspend operator fun invoke() {
		repository.startTrackingWithSavedInterval()
	}
}
