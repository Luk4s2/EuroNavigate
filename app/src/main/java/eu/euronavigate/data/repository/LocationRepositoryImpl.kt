package eu.euronavigate.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import eu.euronavigate.data.model.LocationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class LocationRepositoryImpl : ILocationRepository {

	private var fusedLocationClient: FusedLocationProviderClient? = null
	private var locationCallback: LocationCallback? = null

	private val _locationFlow = MutableSharedFlow<LocationData>(replay = 1)
	override val locationUpdates: Flow<LocationData> = _locationFlow

	@SuppressLint("MissingPermission")
	override fun startLocationUpdates(context: Context, interval: Long) {
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

		locationCallback = object : LocationCallback() {
			override fun onLocationResult(result: LocationResult) {
				for (location in result.locations) {
					println("📡 New location: ${location.latitude}, ${location.longitude}")
					val data = LocationData(
						latitude = location.latitude,
						longitude = location.longitude,
						timestamp = location.time,
						accuracy = location.accuracy,
						provider = location.provider.toString(),
						speed = location.speed,
						altitude = if (location.hasAltitude()) location.altitude else null
					)
					_locationFlow.tryEmit(data)
				}
			}
		}

		val request = LocationRequest.Builder(
			Priority.PRIORITY_HIGH_ACCURACY,
			interval * 60 * 1000
		).build()

		fusedLocationClient?.requestLocationUpdates(request, locationCallback!!, null)
	}

	override fun stopLocationUpdates() {
		locationCallback?.let {
			fusedLocationClient?.removeLocationUpdates(it)
		}
	}
}
