package eu.euronavigate.ui.screens.mapScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eu.euronavigate.data.model.LocationDataModel
import kotlinx.coroutines.launch

@Composable
fun LocationPager(
	locations: List<LocationDataModel>,
	selectedLocation: LocationDataModel?,
	onLocationSelected: (LocationDataModel) -> Unit
) {
	val listState = rememberLazyListState()
	val coroutineScope = rememberCoroutineScope()

	// Automatically scroll to the selected item
	LaunchedEffect(locations.size, selectedLocation) {
		val index = selectedLocation?.let { locations.indexOf(it) } ?: locations.lastIndex
		if (index >= 0) {
			coroutineScope.launch {
				listState.animateScrollToItem(index)
			}
		}
	}

	LazyColumn(
		state = listState,
		modifier = Modifier
			.width(130.dp)
			.height(165.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		items(locations.size) { index ->
			val location = locations[index]
			val isSelected = selectedLocation?.timestamp == location.timestamp

			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 4.dp)
					.clickable { onLocationSelected(location) },
				colors = CardDefaults.cardColors(
					containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceDim,
					contentColor = if (isSelected) Color.White else Color.Black
				)
			) {
				Text(
					text = "Location ${index + 1}",
					modifier = Modifier.padding(12.dp)
				)
			}
		}
	}
}
