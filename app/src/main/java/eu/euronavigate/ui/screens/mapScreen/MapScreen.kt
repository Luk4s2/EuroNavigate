@file:OptIn(ExperimentalMaterial3Api::class)

package eu.euronavigate.ui.screens.mapScreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import eu.euronavigate.data.model.LocationDataModel
import eu.euronavigate.ui.screens.settingsScreen.StatRow
import eu.euronavigate.ui.utils.ModifierFullWidthPadded
import eu.euronavigate.ui.utils.UIConstants
import eu.euronavigate.viewmodel.map.MapViewModel

@Composable
fun MapScreen(
	viewModel: MapViewModel,
	onNavigateToSettings: () -> Unit
) {
	val context = LocalContext.current
	val locationState by viewModel.locationState.collectAsState()
	val selectedLocation by viewModel.selectedLocation.collectAsState()
	val permissionsGranted by viewModel.permissionsGranted.collectAsState()
	val cameraTarget by viewModel.cameraTargetLocation.collectAsState()
	val showBottomSheet by viewModel.showBottomSheet.collectAsState()
	val autoFollow by viewModel.autoFollowEnabled.collectAsState()
	val enableFitPins by viewModel.enableFitPins.collectAsState()

	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	var googleMapRef by remember { mutableStateOf<GoogleMap?>(null) }


	val permissionLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestMultiplePermissions()
	) { permissions ->
		val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
				permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
		viewModel.updatePermissionsGranted(granted)
	}

	// Handle permissions and initial camera focus
	LaunchedEffect(Unit) {
		viewModel.requestPermissionCheck(context)
		if (!permissionsGranted) {
			permissionLauncher.launch(
				arrayOf(
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION
				)
			)
		}
		viewModel.markMoveToLastKnownRequired()
	}

	// Move camera to selected or last location
	LaunchedEffect(cameraTarget, googleMapRef) {
		cameraTarget?.let { latLng ->
			googleMapRef?.animateCamera(
				CameraUpdateFactory.newLatLngZoom(
					latLng,
					UIConstants.DEFAULT_ZOOM
				)
			)
			viewModel.clearCameraTarget()
		}
	}

	Box(modifier = Modifier.fillMaxSize()) {
		if (permissionsGranted) {
			MapContainer(
				context = context,
				viewModel = viewModel,
				modifier = Modifier.fillMaxSize(),
				onMapReady = { googleMapRef = it }
			)
		}

		LaunchedEffect(
			locationState.locations,
			googleMapRef,
			selectedLocation?.timestamp,
			autoFollow
		) {
			googleMapRef?.let {
				viewModel.updateMapWithLocations(it, locationState.locations, selectedLocation)
			}
		}

		// UI Controls inside BoxScope with .align(...) working properly
		TrackingControls(viewModel, onNavigateToSettings, Modifier.align(Alignment.BottomStart))
		PagerOverlay(
			locationState.locations,
			selectedLocation,
			viewModel,
			Modifier.align(Alignment.TopEnd)
		)
		MapControls(
			viewModel,
			googleMapRef,
			locationState.locations,
			enableFitPins,
			Modifier.align(Alignment.TopStart),
			context
		)

		// Bottom sheet
		if (selectedLocation != null && showBottomSheet) {
			ModalBottomSheet(
				onDismissRequest = { viewModel.clearSelectedLocation() },
				sheetState = sheetState
			) {
				val ui = viewModel.getLocationUIModel(selectedLocation!!)
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(UIConstants.PADDING)
				) {
					Text("📍 Location Details", style = MaterialTheme.typography.titleMedium)
					Spacer(Modifier.height(UIConstants.SPACER_MINI))
					StatRow("Latitude", ui.latitude)
					StatRow("Longitude", ui.longitude)
					StatRow("Accuracy", ui.accuracy)
					StatRow("Time", ui.time)
					StatRow("Provider", ui.provider)
				}
			}
		}
	}
}

@Composable
fun MapContainer(
	context: Context,
	viewModel: MapViewModel,
	modifier: Modifier = Modifier,
	onMapReady: (GoogleMap) -> Unit
) {
	AndroidView(
		factory = { ctx ->
			MapView(ctx).apply {
				onCreate(Bundle())
				onResume()
				getMapAsync { map ->
					onMapReady(map)
					viewModel.onMapReady(context)

					map.uiSettings.isMyLocationButtonEnabled = false
					map.uiSettings.isCompassEnabled = false
					@SuppressLint("MissingPermission")
					map.isMyLocationEnabled = true


					map.setOnMarkerClickListener { marker ->
						(marker.tag as? LocationDataModel)?.let {
							viewModel.onMarkerClicked(it)
							true
						} ?: false
					}

					map.setOnCameraIdleListener {
						if (viewModel.movingCameraLocation.value != null) {
							viewModel.triggerBottomSheet(true)
						}
					}
				}
			}
		},
		modifier = modifier
	)
}

@Composable
fun TrackingControls(
	viewModel: MapViewModel,
	onNavigateToSettings: () -> Unit,
	modifier: Modifier
) {
	Column(modifier = modifier.padding(bottom = UIConstants.PADDING)) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceEvenly
		) {
			Button(onClick = { viewModel.startTracking() }) {
				Text(UIConstants.BUTTON_START_TRACKING)
			}
			Button(onClick = { viewModel.stopTracking() }) {
				Text(UIConstants.BUTTON_STOP_TRACKING)
			}
		}
		Spacer(Modifier.height(UIConstants.SPACER_MINI))
		Button(onClick = onNavigateToSettings, modifier = ModifierFullWidthPadded) {
			Text(UIConstants.BUTTON_GO_TO_SETTINGS)
		}
	}
}

@Composable
fun PagerOverlay(
	locations: List<LocationDataModel>,
	selectedLocation: LocationDataModel?,
	viewModel: MapViewModel,
	modifier: Modifier
) {
	Box(modifier = modifier.padding(end = UIConstants.PADDING, top = UIConstants.PADDING)) {
		LocationPager(
			locations = locations,
			selectedLocation = selectedLocation,
			onLocationSelected = {
				viewModel.triggerBottomSheet(false)
				viewModel.selectAndCenterLocation(it)
				viewModel.updateMovingCameraLocation(it)
			}
		)
	}
}

@Composable
fun MapControls(
	viewModel: MapViewModel,
	googleMap: GoogleMap?,
	locations: List<LocationDataModel>,
	enableFitPins: Boolean,
	modifier: Modifier,
	context: Context
) {
	Column(
		modifier = modifier.padding(start = UIConstants.PADDING, top = UIConstants.PADDING),
		verticalArrangement = Arrangement.spacedBy(UIConstants.SPACER_MINI)
	) {
		Row {
			Button(onClick = { googleMap?.animateCamera(CameraUpdateFactory.zoomIn()) }) {
				Text("+")
			}
			Spacer(Modifier.width(UIConstants.SPACER_SMALL))
			Button(
				enabled = enableFitPins,
				onClick = {
					val bounds = viewModel.getCameraBounds(locations)
					googleMap?.animateCamera(
						CameraUpdateFactory.newLatLngBounds(bounds, UIConstants.CAMERA_PADDING)
					)
				}
			) {
				Text(UIConstants.BUTTON_FIT_PINS)
			}
		}
		Row {
			Button(onClick = { googleMap?.animateCamera(CameraUpdateFactory.zoomOut()) }) {
				Text("–")
			}
			Spacer(Modifier.width(UIConstants.SPACER_SMALL))
			Button(onClick = { viewModel.fetchLastKnownLocation(context) }) {
				Icon(Icons.Default.Home, contentDescription = "My Location")
			}
		}
	}
}
