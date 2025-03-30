package eu.euronavigate.ui.screens.settingsScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import eu.euronavigate.data.local.SettingsDataStore
import eu.euronavigate.ui.components.CustomSnackbarHost
import eu.euronavigate.ui.utils.UIConstants
import eu.euronavigate.viewmodel.MapViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MapViewModel, navController: NavHostController) {
	val context = LocalContext.current
	var interval by remember { mutableStateOf("") }
	var isLoading by remember { mutableStateOf(true) }
	val locations by viewModel.locationState.collectAsState()
	val keyboardController = LocalSoftwareKeyboardController.current
	val scrollState = rememberScrollState()
	val snackbarHostState = remember { SnackbarHostState() }
	val coroutineScope = rememberCoroutineScope()

	LaunchedEffect(Unit) {
		val saved = SettingsDataStore.getInterval(context)
		interval = saved.toString()
		isLoading = false
	}

	val saveInterval = {
		keyboardController?.hide()
		val intervalValue = interval.toLongOrNull() ?: UIConstants.FALLBACK_INTERVAL

		viewModel.updateTrackingInterval(intervalValue)
		viewModel.restartTrackingIfRunning(context)

		coroutineScope.launch {
			SettingsDataStore.saveInterval(context, intervalValue)
			interval = intervalValue.toString()
			snackbarHostState.showSnackbar(UIConstants.LABEL_SNACKBAR_SAVING)
		}
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(UIConstants.LABEL_SETTINGS_TITLE) },
				navigationIcon = {
					IconButton(onClick = { navController.popBackStack() }) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = UIConstants.LABEL_BACK
						)
					}
				}
			)
		},
		snackbarHost = {
			CustomSnackbarHost(hostState = snackbarHostState)
		}
	) { innerPadding ->
		if (isLoading) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding),
				contentAlignment = Alignment.Center
			) {
				CircularProgressIndicator()
			}
		} else {
			Column(
				modifier = Modifier
					.padding(innerPadding)
					.padding(horizontal = UIConstants.PADDING, vertical = UIConstants.SPACER_MEDIUM)
					.verticalScroll(scrollState)
			) {
				OutlinedTextField(
					value = interval,
					onValueChange = {
						if (it.length <= 3 && it.all { char -> char.isDigit() }) {
							interval = it
						}
					},
					label = { Text(UIConstants.LABEL_TRACKING_INTERVAL) },
					modifier = Modifier.fillMaxWidth(),
					keyboardOptions = KeyboardOptions.Default.copy(
						keyboardType = KeyboardType.Number,
						imeAction = ImeAction.Done
					),
					keyboardActions = KeyboardActions(
						onDone = { saveInterval() }
					)
				)

				Spacer(modifier = Modifier.height(UIConstants.SPACER_LARGE))

				Button(
					onClick = { saveInterval() },
					modifier = Modifier.fillMaxWidth()
				) {
					Text(UIConstants.BUTTON_SAVE_INTERVAL)
				}

				Spacer(modifier = Modifier.height(UIConstants.SPACER_LARGE))

				Button(
					onClick = {
						exportAndShareLocations(context, locations.locations)
					},
					modifier = Modifier.fillMaxWidth()
				) {
					Text(UIConstants.BUTTON_EXPORT_JSON)
				}

				StatsSection(locations = locations.locations)
			}
		}
	}
}
