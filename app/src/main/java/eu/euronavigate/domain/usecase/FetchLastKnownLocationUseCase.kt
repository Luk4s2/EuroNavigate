package eu.euronavigate.domain.usecase

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FetchLastKnownLocationUseCase @Inject constructor() {

	suspend operator fun invoke(context: Context): LatLng? {
		val fine =
			ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
		val coarse =
			ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

		if (fine != PackageManager.PERMISSION_GRANTED && coarse != PackageManager.PERMISSION_GRANTED) {
			return null
		}

		return try {
			val client = LocationServices.getFusedLocationProviderClient(context)
			val location = client.lastLocation.await()
			location?.let { LatLng(it.latitude, it.longitude) }
		} catch (e: Exception) {
			null
		}
	}
}
