package eu.euronavigate.ui.utils

import eu.euronavigate.data.model.LocationDataModel
import eu.euronavigate.data.model.TrackingStatsModel
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object LocationHelper {

	private fun calculateDistance(locations: List<LocationDataModel>): Double {
		if (locations.size < 2) return 0.0
		var distance = 0.0
		for (i in 1 until locations.size) {
			val a = locations[i - 1]
			val b = locations[i]
			distance += haversine(a.latitude, a.longitude, b.latitude, b.longitude)
		}
		return distance
	}

	fun calculateStats(locations: List<LocationDataModel>): TrackingStatsModel {
		return TrackingStatsModel(
			distance = calculateDistance(locations),
			avgSpeed = getAverageSpeed(locations),
			minSpeed = getMinSpeed(locations),
			maxSpeed = getMaxSpeed(locations),
			altitude = locations.lastOrNull()?.altitude
		)
	}

	fun isDuplicateLocation(newLoc: LocationDataModel, lastLoc: LocationDataModel?): Boolean {
		return lastLoc != null &&
				abs(lastLoc.latitude - newLoc.latitude) < 0.00001 &&
				abs(lastLoc.longitude - newLoc.longitude) < 0.00001 &&
				abs(lastLoc.timestamp - newLoc.timestamp) < 2000
	}

	private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
		val r = 6371000.0 // meters
		val dLat = Math.toRadians(lat2 - lat1)
		val dLon = Math.toRadians(lon2 - lon1)
		val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) *
				cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
		return 2 * r * asin(sqrt(a))
	}

	private fun getAverageSpeed(locations: List<LocationDataModel>): Double? {
		val speeds = locations.map { it.speed }
		return if (speeds.isNotEmpty()) speeds.average() else null
	}

	private fun getMinSpeed(locations: List<LocationDataModel>): Float? =
		locations.minOfOrNull { it.speed }

	private fun getMaxSpeed(locations: List<LocationDataModel>): Float? =
		locations.maxOfOrNull { it.speed }
}
