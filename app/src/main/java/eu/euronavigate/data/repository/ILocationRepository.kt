package eu.euronavigate.data.repository

import android.content.Context
import eu.euronavigate.data.model.LocationData
import kotlinx.coroutines.flow.Flow

interface ILocationRepository {
	val locationUpdates: Flow<LocationData>
	fun startLocationUpdates(context: Context, interval: Long)
	fun stopLocationUpdates()
}
