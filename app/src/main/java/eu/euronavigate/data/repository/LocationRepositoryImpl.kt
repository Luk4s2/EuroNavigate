package eu.euronavigate.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.euronavigate.data.local.SettingsDataStore
import eu.euronavigate.data.model.LocationDataModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
	@ApplicationContext private val context: Context,
	private val settingsDataStore: SettingsDataStore
) : ILocationRepository {

	private var fusedLocationClient: FusedLocationProviderClient? = null
	private lateinit var locationCallback: LocationCallback

	private val _locationChannel = Channel<LocationDataModel>(Channel.BUFFERED)
	override val locationUpdates: Flow<LocationDataModel> = _locationChannel.receiveAsFlow()

	@SuppressLint("MissingPermission")
	override fun startLocationUpdates(interval: Long) {
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

		locationCallback = object : LocationCallback() {
			override fun onLocationResult(result: LocationResult) {
				for (location in result.locations) {
					val data = LocationDataModel(
						latitude = location.latitude,
						longitude = location.longitude,
						timestamp = location.time,
						accuracy = location.accuracy,
						provider = location.provider.toString(),
						speed = location.speed,
						altitude = if (location.hasAltitude()) location.altitude else null
					)
					_locationChannel.trySend(data)
				}
			}
		}

		val request = LocationRequest.Builder(
			Priority.PRIORITY_HIGH_ACCURACY,
			interval * 60 * 1000
		).build()

		fusedLocationClient?.requestLocationUpdates(request, locationCallback, null)
	}

	override fun stopLocationUpdates() {
		locationCallback.let {
			fusedLocationClient?.removeLocationUpdates(it)
		}
	}

	@SuppressLint("MissingPermission")
	override suspend fun startTrackingWithSavedInterval() {
		val client = LocationServices.getFusedLocationProviderClient(context)
		fusedLocationClient = client

		val savedInterval = settingsDataStore.getInterval()
		startLocationUpdates(savedInterval)
	}
}
