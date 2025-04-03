package eu.euronavigate.data.repository

import eu.euronavigate.data.model.LocationDataModel
import kotlinx.coroutines.flow.Flow

interface ILocationRepository {
	val locationUpdates: Flow<LocationDataModel>
	fun startLocationUpdates(interval: Long)
	suspend fun startTrackingWithSavedInterval()
	fun stopLocationUpdates()
}