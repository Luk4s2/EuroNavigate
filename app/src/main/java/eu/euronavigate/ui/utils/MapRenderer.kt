package eu.euronavigate.ui.utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import eu.euronavigate.data.model.LocationDataModel

object MapRenderer {

	fun renderLocations(
		map: GoogleMap,
		locations: List<LocationDataModel>,
		selectedLocation: LocationDataModel?,
		autoFollow: Boolean
	) {
		map.clear()

		if (locations.isEmpty()) return

		val polyline = PolylineOptions()
			.width(8f)
			.color(android.graphics.Color.BLUE)

		locations.forEach { loc ->
			val latLng = LatLng(loc.latitude, loc.longitude)
			map.addMarker(MarkerOptions().position(latLng).title("Tap for details"))?.tag = loc
			polyline.add(latLng)
		}
		map.addPolyline(polyline)

		val target = when {
			selectedLocation != null -> LatLng(
				selectedLocation.latitude,
				selectedLocation.longitude
			)

			autoFollow && locations.isNotEmpty() -> LatLng(
				locations.last().latitude,
				locations.last().longitude
			)

			else -> return
		}

		map.animateCamera(CameraUpdateFactory.newLatLngZoom(target, UIConstants.DEFAULT_ZOOM))

		selectedLocation?.let {
			map.addCircle(
				CircleOptions()
					.center(LatLng(it.latitude, it.longitude))
					.radius(30.0)
					.strokeColor(android.graphics.Color.argb(200, 108, 99, 255))
					.fillColor(android.graphics.Color.argb(70, 108, 99, 255))
					.strokeWidth(4f)
			)
		}
	}
}
