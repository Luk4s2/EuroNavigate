@file:OptIn(ExperimentalMaterial3Api::class)

package eu.euronavigate.ui.screens.mapScreen

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
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
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import eu.euronavigate.data.model.LocationData
import eu.euronavigate.ui.navigation.ObserveLocationPermission
import eu.euronavigate.ui.screens.settingsScreen.StatRow
import eu.euronavigate.ui.utils.ModifierFullWidthPadded
import eu.euronavigate.ui.utils.UIConstants
import eu.euronavigate.viewmodel.MapViewModel
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Locale

@Composable
fun MapScreen(
	viewModel: MapViewModel,
	onNavigateToSettings: () -> Unit
) {
	val context = LocalContext.current
	val activity = context as? Activity
	val locationState by viewModel.locationState.collectAsState()
	val selectedLocation by viewModel.selectedLocation

	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	var googleMapRef by remember { mutableStateOf<GoogleMap?>(null) }
	var didMoveCamera by remember { mutableStateOf(false) }
	var movingCameraLocation by remember { mutableStateOf<LocationData?>(null) }
	var showBottomSheet by remember { mutableStateOf(false) }
	val permissionsGranted = remember { mutableStateOf(false) }

	fun handleLocationSelection(location: LocationData) {
		showBottomSheet = false
		viewModel.selectLocation(location)

		val latLng = LatLng(location.latitude, location.longitude)
		val cameraPosition = CameraPosition.Builder()
			.target(latLng)
			.zoom(UIConstants.DEFAULT_ZOOM)
			.build()

		googleMapRef?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
		movingCameraLocation = location
	}

	fun moveCameraToLocation(location: LocationData) {
		val cameraPosition = CameraPosition.Builder()
			.target(LatLng(location.latitude, location.longitude))
			.zoom(UIConstants.DEFAULT_ZOOM)
			.build()
		googleMapRef?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
	}
	ObserveLocationPermission(permissionsGranted)

	// Request permissions + load interval
	LaunchedEffect(Unit) {
		viewModel.initTrackingInterval(context)

		val fine = ActivityCompat.checkSelfPermission(
			context,
			Manifest.permission.ACCESS_FINE_LOCATION
		)
		val coarse = ActivityCompat.checkSelfPermission(
			context,
			Manifest.permission.ACCESS_COARSE_LOCATION
		)

		if (fine != PackageManager.PERMISSION_GRANTED || coarse != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(
				activity!!,
				arrayOf(
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION
				),
				1001
			)
		} else {
			permissionsGranted.value = true
		}
	}

	LaunchedEffect(context) {
		val fine = ActivityCompat.checkSelfPermission(
			context,
			Manifest.permission.ACCESS_FINE_LOCATION
		)
		val coarse = ActivityCompat.checkSelfPermission(
			context,
			Manifest.permission.ACCESS_COARSE_LOCATION
		)

		if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
			permissionsGranted.value = true
		}
	}

	// Move to last known location on first load
	LaunchedEffect(googleMapRef) {
		if (googleMapRef != null && !didMoveCamera) {
			val client = LocationServices.getFusedLocationProviderClient(context)
			try {
				val lastLoc = client.lastLocation.await()
				lastLoc?.let {
					val latLng = LatLng(it.latitude, it.longitude)
					googleMapRef?.moveCamera(
						CameraUpdateFactory.newLatLngZoom(latLng, UIConstants.DEFAULT_ZOOM)
					)
					didMoveCamera = true
				}
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	if (selectedLocation != null && showBottomSheet) {
		ModalBottomSheet(
			onDismissRequest = { viewModel.clearSelectedLocation() },
			sheetState = sheetState
		) {
			val location = selectedLocation
			if (location != null) {
				val formattedTime = remember(location.timestamp) {
					java.text.SimpleDateFormat("EEE, MMM d yyyy - HH:mm:ss", Locale.getDefault())
						.format(Date(location.timestamp))
				}

				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(UIConstants.PADDING)
				) {
					Text(
						text = "📍 Location Details",
						style = MaterialTheme.typography.titleMedium
					)

					Spacer(modifier = Modifier.height(UIConstants.SPACER_MINI))

					StatRow("Latitude", "%.5f".format(location.latitude))
					StatRow("Longitude", "%.5f".format(location.longitude))
					StatRow("Accuracy", "${location.accuracy} m")
					StatRow("Time", formattedTime)
					StatRow("Provider", location.provider)
				}
			}
		}
	}

	Box(modifier = Modifier.fillMaxSize()) {
		if (permissionsGranted.value) {
			AndroidView(
				factory = { ctx ->
					MapView(ctx).apply {
						onCreate(Bundle())
						onResume()
						getMapAsync { map ->
							googleMapRef = map
							if (
								ActivityCompat.checkSelfPermission(
									context,
									Manifest.permission.ACCESS_FINE_LOCATION
								) == PackageManager.PERMISSION_GRANTED ||
								ActivityCompat.checkSelfPermission(
									context,
									Manifest.permission.ACCESS_COARSE_LOCATION
								) == PackageManager.PERMISSION_GRANTED
							) {
								map.uiSettings.isMyLocationButtonEnabled = false
								map.isMyLocationEnabled = true
								map.uiSettings.isCompassEnabled = false
							}

							map.setOnMarkerClickListener { marker ->
								val data = marker.tag as? LocationData
								data?.let {
									handleLocationSelection(it)
									true
								} ?: false
							}

							map.setOnCameraIdleListener {
								movingCameraLocation?.let {
									showBottomSheet = true
									movingCameraLocation = null
								}
							}
						}
					}
				},
				modifier = Modifier.matchParentSize()
			)
		}

		// Polyline + Marker drawing
		LaunchedEffect(locationState.locations, googleMapRef, selectedLocation?.timestamp) {
			val map = googleMapRef ?: return@LaunchedEffect
			val newLocation = locationState.locations.lastOrNull()
			if (newLocation != null && selectedLocation == null && !didMoveCamera) {
				moveCameraToLocation(newLocation)
				didMoveCamera = true
			}
			if (locationState.locations.isNotEmpty()) {
				map.clear()

				val polylineOptions = PolylineOptions().width(8f).color(Color.BLUE)

				locationState.locations.forEach { loc ->
					val latLng = LatLng(loc.latitude, loc.longitude)
					val marker = map.addMarker(
						MarkerOptions().position(latLng).title("Tap for details")
					)
					marker?.tag = loc
					polylineOptions.add(latLng)
				}

				map.addPolyline(polylineOptions)

				selectedLocation?.let {
					val circleCenter = LatLng(it.latitude, it.longitude)
					map.addCircle(
						com.google.android.gms.maps.model.CircleOptions()
							.center(circleCenter)
							.radius(30.0) // meters
							.strokeColor(Color.argb(200, 108, 99, 255))
							.fillColor(Color.argb(70, 108, 99, 255))
							.strokeWidth(4f)
					)
				}
			}
		}

		Column(
			modifier = Modifier
				.align(Alignment.BottomStart)
				.padding(bottom = UIConstants.PADDING)
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceEvenly
			) {
				Button(onClick = {
					viewModel.startTracking(context)
				}) {
					Text(UIConstants.BUTTON_START_TRACKING)
				}
				Button(onClick = {
					viewModel.stopTracking()
				}) {
					Text(UIConstants.BUTTON_STOP_TRACKING)
				}
			}
			Spacer(modifier = Modifier.height(UIConstants.SPACER_MINI))
			Button(
				onClick = onNavigateToSettings,
				modifier = ModifierFullWidthPadded
			) {
				Text(UIConstants.BUTTON_GO_TO_SETTINGS)
			}
		}
		Box(
			modifier = Modifier
				.align(Alignment.TopEnd)
				.padding(end = UIConstants.PADDING, top = UIConstants.PADDING)
		) {
			LocationPager(
				locations = locationState.locations,
				selectedLocation = selectedLocation,
				onLocationSelected = { location ->
					handleLocationSelection(location)
				}
			)
		}


		// Zoom
		Column(
			modifier = Modifier
				.align(Alignment.TopStart)
				.padding(start = UIConstants.PADDING, top = UIConstants.PADDING),
			verticalArrangement = Arrangement.spacedBy(UIConstants.SPACER_MINI)
		) {

			Row {
				Button(onClick = {
					googleMapRef?.animateCamera(CameraUpdateFactory.zoomIn())
				}) {
					Text("+")
				}
				Spacer(modifier = Modifier.width(UIConstants.SPACER_SMALL))
				Button(
					enabled = locationState.locations.size >= 2,
					onClick = {
						val map = googleMapRef ?: return@Button
						val locations = locationState.locations
						if (locations.size < 2) return@Button

						val builder = LatLngBounds.Builder()
						locations.forEach {
							builder.include(LatLng(it.latitude, it.longitude))
						}

						val bounds = builder.build()
						val padding = UIConstants.CAMERA_PADDING
						map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
					}
				) {
					Text(UIConstants.BUTTON_FIT_PINS)
				}

			}
			Row {
				Button(onClick = {
					googleMapRef?.animateCamera(CameraUpdateFactory.zoomOut())
				}) {
					Text("–")
				}
				Spacer(modifier = Modifier.width(UIConstants.SPACER_SMALL))
				// Custom My Location button
				Button(
					onClick = {
						val fusedClient = LocationServices.getFusedLocationProviderClient(context)
						fusedClient.lastLocation.addOnSuccessListener { location ->
							location?.let { loc ->
								val latLng = LatLng(loc.latitude, loc.longitude)

								val cameraPosition = CameraPosition.Builder()
									.target(latLng)
									.zoom(UIConstants.DEFAULT_ZOOM)
									.bearing(loc.bearing)
									.build()

								googleMapRef?.animateCamera(
									CameraUpdateFactory.newCameraPosition(cameraPosition)
								)
							}
						}
					},
				) {
					Icon(
						imageVector = Icons.Filled.Home,
						contentDescription = "My Location",
					)
				}
			}

		}
	}
}
