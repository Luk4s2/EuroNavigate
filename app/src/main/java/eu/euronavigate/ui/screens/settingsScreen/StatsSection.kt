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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.euronavigate.data.model.TrackingStatsModel
import eu.euronavigate.ui.utils.LocalizedStrings
import eu.euronavigate.ui.utils.UIConstants
import eu.euronavigate.ui.utils.formatSpeed

@Composable
fun StatsSection(stats: TrackingStatsModel) {
	Column(
		modifier = Modifier
			.padding(top = UIConstants.SPACER_MEDIUM)
			.fillMaxWidth()
	) {
		Text(
			text = LocalizedStrings.LABEL_TITLE,
			style = MaterialTheme.typography.titleMedium
		)

		Spacer(Modifier.height(UIConstants.SPACER_MINI))

		StatRow(
			label = LocalizedStrings.LABEL_TOTAL_DISTANCE,
			value = "%.2f m".format(stats.distance)
		)
		StatRow(
			label = LocalizedStrings.LABEL_AVG_SPEED,
			value = stats.avgSpeed?.formatSpeed() ?: "N/A"
		)
		StatRow(
			label = LocalizedStrings.LABEL_MIN_SPEED,
			value = stats.minSpeed?.formatSpeed() ?: "N/A"
		)
		StatRow(
			label = LocalizedStrings.LABEL_MAX_SPEED,
			value = stats.maxSpeed?.formatSpeed() ?: "N/A"
		)

		stats.altitude?.let {
			StatRow(label = LocalizedStrings.LABEL_ALTITUDE, value = "%.1f m".format(it))
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
