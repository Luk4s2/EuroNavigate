package eu.euronavigate.ui.screens.settingsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.euronavigate.data.model.LocationData
import eu.euronavigate.ui.utils.StatStrings
import eu.euronavigate.ui.utils.UIConstants
import eu.euronavigate.ui.utils.averageOrNull
import eu.euronavigate.ui.utils.formatSpeed
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun StatsSection(locations: List<LocationData>) {
	val distance = remember(locations) { calculateTotalDistance(locations) }

	val speeds = locations.map { it.speed.toDouble() }
	val avgSpeed = speeds.averageOrNull()
	val minSpeed = speeds.minOrNull()
	val maxSpeed = speeds.maxOrNull()
	val altitude = locations.lastOrNull()?.altitude

	Column(
		modifier = Modifier
			.padding(top = UIConstants.SPACER_MEDIUM)
			.fillMaxWidth()
	) {
		Text(
			text = StatStrings.LABEL_TITLE,
			style = MaterialTheme.typography.titleMedium
		)

		Spacer(Modifier.height(UIConstants.SPACER_MINI))

		StatRow(label = StatStrings.LABEL_TOTAL_DISTANCE, value = "%.2f m".format(distance))
		StatRow(label = StatStrings.LABEL_AVG_SPEED, value = avgSpeed?.formatSpeed() ?: "N/A")
		StatRow(label = StatStrings.LABEL_MIN_SPEED, value = minSpeed?.formatSpeed() ?: "N/A")
		StatRow(label = StatStrings.LABEL_MAX_SPEED, value = maxSpeed?.formatSpeed() ?: "N/A")

		if (altitude != null) {
			StatRow(label = StatStrings.LABEL_ALTITUDE, value = "%.1f m".format(altitude))
		}
	}
}

@Composable
fun StatRow(label: String, value: String) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 2.dp),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Text(text = label, style = MaterialTheme.typography.bodyMedium)
		Text(text = value, style = MaterialTheme.typography.bodyMedium)
	}
}

private fun calculateTotalDistance(locations: List<LocationData>): Double {
	if (locations.size < 2) return 0.0
	return locations.zipWithNext { a, b ->
		haversine(a.latitude, a.longitude, b.latitude, b.longitude)
	}.sum()
}

private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
	val r = 6371000.0 // Earth radius in meters
	val dLat = Math.toRadians(lat2 - lat1)
	val dLon = Math.toRadians(lon2 - lon1)
	val a = sin(dLat / 2).pow(2.0) +
			cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
			sin(dLon / 2).pow(2.0)
	val c = 2 * atan2(sqrt(a), sqrt(1 - a))
	return r * c
}

