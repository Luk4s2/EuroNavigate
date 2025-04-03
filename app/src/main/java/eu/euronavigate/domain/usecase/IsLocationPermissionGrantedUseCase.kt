package eu.euronavigate.domain.usecase

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import javax.inject.Inject

class IsLocationPermissionGrantedUseCase @Inject constructor() {
	operator fun invoke(context: Context): Boolean {
		val fine =
			ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
		val coarse =
			ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
		return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
	}
}