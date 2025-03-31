package eu.euronavigate.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.euronavigate.data.local.SettingsDataStore
import eu.euronavigate.data.model.LocationData
import eu.euronavigate.data.repository.ILocationRepository
import eu.euronavigate.data.repository.LocationRepositoryImpl
import eu.euronavigate.ui.utils.UIConstants
import eu.euronavigate.ui.utils.isDuplicateLocation
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LocationState(val locations: List<LocationData> = emptyList())

class MapViewModel : ViewModel() {

	private val repository: ILocationRepository = LocationRepositoryImpl()

	private val _locationState = MutableStateFlow(LocationState())
	val locationState: StateFlow<LocationState> = _locationState

	private val _selectedLocation = mutableStateOf<LocationData?>(null)
	val selectedLocation: State<LocationData?> = _selectedLocation

	private var trackingInterval: Long = UIConstants.FALLBACK_INTERVAL
	private var trackingJob: Job? = null
	private var isTracking = false

	fun initTrackingInterval(context: Context) {
		viewModelScope.launch {
			val saved = SettingsDataStore.getInterval(context)
			updateTrackingInterval(saved)
		}
	}

	fun updateTrackingInterval(newInterval: Long) {
		trackingInterval = newInterval
	}

	fun restartTrackingIfRunning(context: Context) {
		if (isTracking) {
			stopTracking()
			clearSelectedLocation()
			startTracking(context)

		}
	}

	fun selectLocation(location: LocationData) {
		_selectedLocation.value = location
	}

	fun clearSelectedLocation() {
		_selectedLocation.value = null
	}

	fun startTracking(context: Context) {
		if (trackingJob != null) return
		isTracking = true
		repository.startLocationUpdates(context, trackingInterval)

		trackingJob = viewModelScope.launch {
			repository.locationUpdates.collect { loc ->
				val currentList = _locationState.value.locations
				val last = currentList.lastOrNull()

				val isDuplicate = isDuplicateLocation(loc, last)

				if (!isDuplicate) {
					_locationState.value = _locationState.value.copy(
						locations = currentList + loc
					)
				}
			}
		}
	}

	fun stopTracking() {
		repository.stopLocationUpdates()
		trackingJob?.cancel()
		trackingJob = null
		isTracking = false
	}
}
