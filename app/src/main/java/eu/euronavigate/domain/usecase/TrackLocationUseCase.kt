package eu.euronavigate.domain.usecase

import eu.euronavigate.data.model.LocationDataModel
import eu.euronavigate.data.repository.ILocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TrackLocationUseCase @Inject constructor(
	private val repository: ILocationRepository
) {
	operator fun invoke(): Flow<LocationDataModel> = repository.locationUpdates
}