package eu.euronavigate.viewmodel.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.euronavigate.data.model.LocationDataModel
import eu.euronavigate.data.model.LocationUIModel
import eu.euronavigate.data.model.TrackingStatsModel
import eu.euronavigate.domain.usecase.FetchLastKnownLocationUseCase
import eu.euronavigate.domain.usecase.IsLocationPermissionGrantedUseCase
import eu.euronavigate.domain.usecase.StartTrackingUseCase
import eu.euronavigate.domain.usecase.StopTrackingUseCase
import eu.euronavigate.domain.usecase.TrackLocationUseCase
import eu.euronavigate.ui.utils.LocationHelper
import eu.euronavigate.ui.utils.MapRenderer
import eu.euronavigate.ui.utils.UIConstants
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class LocationState(val locations: List<LocationDataModel> = emptyList())

@HiltViewModel
class MapViewModel @Inject constructor(
	private val startTrackingUseCase: StartTrackingUseCase,
	private val trackLocationUseCase: TrackLocationUseCase,
	private val stopTrackingUseCase: StopTrackingUseCase,
	private val fetchLastKnownLocationUseCase: FetchLastKnownLocationUseCase,
	private val isLocationPermissionGrantedUseCase: IsLocationPermissionGrantedUseCase
) : ViewModel() {

	private val _locationState = MutableStateFlow(LocationState())
	val locationState: StateFlow<LocationState> = _locationState

	private val _trackingStatsModel =
		MutableStateFlow(TrackingStatsModel(0.0, null, null, null, null))
	val trackingStatsModel: StateFlow<TrackingStatsModel> = _trackingStatsModel

	private val _selectedLocation = MutableStateFlow<LocationDataModel?>(null)
	val selectedLocation: StateFlow<LocationDataModel?> = _selectedLocation

	private val _permissionsGranted = MutableStateFlow(false)
	val permissionsGranted: StateFlow<Boolean> = _permissionsGranted

	private val _shouldMoveToLastKnownLocation = MutableStateFlow(false)
	private val shouldMoveToLastKnownLocation: StateFlow<Boolean> = _shouldMoveToLastKnownLocation

	private val _cameraTargetLocation = MutableStateFlow<LatLng?>(null)
	val cameraTargetLocation: StateFlow<LatLng?> = _cameraTargetLocation

	private val _showBottomSheet = MutableStateFlow(false)
	val showBottomSheet: StateFlow<Boolean> = _showBottomSheet

	private val _autoFollowEnabled = MutableStateFlow(true)
	val autoFollowEnabled: StateFlow<Boolean> = _autoFollowEnabled

	private val _movingCameraLocation = MutableStateFlow<LocationDataModel?>(null)
	val movingCameraLocation: StateFlow<LocationDataModel?> = _movingCameraLocation

	val enableFitPins: StateFlow<Boolean> = locationState
		.map { it.locations.size >= 2 }
		.stateIn(viewModelScope, SharingStarted.Eagerly, false)

	private var trackingJob: Job? = null
	private var isTracking = false
	private var trackingInterval: Long = UIConstants.FALLBACK_INTERVAL


	fun updatePermissionsGranted(granted: Boolean) {
		_permissionsGranted.value = granted
	}

	fun requestPermissionCheck(context: Context) {
		_permissionsGranted.value = isLocationPermissionGrantedUseCase(context)
	}

	fun markMoveToLastKnownRequired() {
		_shouldMoveToLastKnownLocation.value = true
	}

	private fun clearMoveToLastKnownFlag() {
		_shouldMoveToLastKnownLocation.value = false
	}

	fun clearCameraTarget() {
		_cameraTargetLocation.value = null
	}

	fun onMapReady(context: Context) {
		if (shouldMoveToLastKnownLocation.value) {
			viewModelScope.launch {
				_cameraTargetLocation.value = fetchLastKnownLocationUseCase(context)
				clearMoveToLastKnownFlag()
			}
		}
	}

	fun fetchLastKnownLocation(context: Context) {
		viewModelScope.launch {
			_cameraTargetLocation.value = fetchLastKnownLocationUseCase(context)
		}
	}

	fun triggerBottomSheet(show: Boolean) {
		_showBottomSheet.value = show
	}

	fun updateMovingCameraLocation(location: LocationDataModel?) {
		_movingCameraLocation.value = location
	}

	fun selectAndCenterLocation(location: LocationDataModel) {
		_selectedLocation.value = location
		_cameraTargetLocation.value = LatLng(location.latitude, location.longitude)
	}

	fun clearSelectedLocation() {
		_selectedLocation.value = null
	}

	fun onMarkerClicked(location: LocationDataModel) {
		triggerBottomSheet(false)
		selectAndCenterLocation(location)
		updateMovingCameraLocation(location)
	}


	fun updateMapWithLocations(
		map: GoogleMap,
		locations: List<LocationDataModel>,
		selected: LocationDataModel?
	) {
		MapRenderer.renderLocations(map, locations, selected, autoFollowEnabled.value)
	}

	fun getCameraBounds(locations: List<LocationDataModel>): LatLngBounds {
		val builder = LatLngBounds.builder()
		locations.forEach { builder.include(LatLng(it.latitude, it.longitude)) }
		return builder.build()
	}

	fun getLocationUIModel(location: LocationDataModel): LocationUIModel {
		val formattedTime = SimpleDateFormat("EEE, MMM d yyyy - HH:mm:ss", Locale.getDefault())
			.format(Date(location.timestamp))
		return LocationUIModel(
			latitude = "%.5f".format(location.latitude),
			longitude = "%.5f".format(location.longitude),
			accuracy = "${location.accuracy} m",
			time = formattedTime,
			provider = location.provider
		)
	}

	fun updateTrackingInterval(newInterval: Long) {
		trackingInterval = newInterval
	}

	fun restartTrackingIfRunning() {
		if (isTracking) {
			stopTracking()
			clearSelectedLocation()
			startTracking()
		}
	}

	fun startTracking() {
		if (trackingJob != null) return
		isTracking = true

		trackingJob = viewModelScope.launch {
			startTrackingUseCase()
			trackLocationUseCase()
				.collect { loc ->
					val currentList = _locationState.value.locations
					val last = currentList.lastOrNull()

					if (!LocationHelper.isDuplicateLocation(loc, last)) {
						val updatedList = currentList + loc
						_locationState.value = _locationState.value.copy(locations = updatedList)
						_trackingStatsModel.value = LocationHelper.calculateStats(updatedList)
					}
				}
		}
	}

	fun stopTracking() {
		stopTrackingUseCase()
		trackingJob?.cancel()
		trackingJob = null
		isTracking = false
	}
}
