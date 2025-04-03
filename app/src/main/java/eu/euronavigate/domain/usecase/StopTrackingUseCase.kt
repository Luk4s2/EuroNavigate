package eu.euronavigate.domain.usecase

import eu.euronavigate.data.repository.ILocationRepository
import javax.inject.Inject

class StopTrackingUseCase @Inject constructor(
	private val repository: ILocationRepository
) {
	operator fun invoke() {
		repository.stopLocationUpdates()
	}
}
