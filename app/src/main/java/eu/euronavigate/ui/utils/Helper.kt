package eu.euronavigate.ui.utils

import eu.euronavigate.data.model.LocationData
import kotlin.math.abs

fun isDuplicateLocation(newLoc: LocationData, lastLoc: LocationData?): Boolean {
	return lastLoc != null &&
			abs(lastLoc.latitude - newLoc.latitude) < 0.00001 &&
			abs(lastLoc.longitude - newLoc.longitude) < 0.00001 &&
			abs(lastLoc.timestamp - newLoc.timestamp) < 2000
}