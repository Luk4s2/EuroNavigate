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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import eu.euronavigate.ui.components.CustomSnackbarHost
import eu.euronavigate.ui.utils.UIConstants
import eu.euronavigate.ui.utils.exportAndShareLocations
import eu.euronavigate.viewmodel.map.MapViewModel
import eu.euronavigate.viewmodel.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
	viewModel: SettingsViewModel,
	navController: NavHostController,
	mapViewModel: MapViewModel
) {
	val interval by viewModel.interval
	val isLoading by viewModel.isLoading
	val keyboardController = LocalSoftwareKeyboardController.current
	val scrollState = rememberScrollState()
	val snackbarHostState = remember { SnackbarHostState() }
	val coroutineScope = rememberCoroutineScope()
	val context = LocalContext.current

	val stats by mapViewModel.trackingStatsModel.collectAsState()
	val locationState by mapViewModel.locationState.collectAsState()
	val locations = locationState.locations

	LaunchedEffect(Unit) {
		viewModel.initializeIntervalSettings()
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
							viewModel.updateIntervalInput(it)
						}
					},
					label = { Text(UIConstants.LABEL_TRACKING_INTERVAL) },
					modifier = Modifier.fillMaxWidth(),
					keyboardOptions = KeyboardOptions.Default.copy(
						keyboardType = KeyboardType.Number,
						imeAction = ImeAction.Done
					),
					keyboardActions = KeyboardActions(onDone = {
						viewModel.onSaveIntervalWithUiFeedback(
							mapViewModel,
							keyboardController,
							coroutineScope,
							snackbarHostState
						)
					})
				)

				Spacer(modifier = Modifier.height(UIConstants.SPACER_LARGE))

				Button(onClick = {
					viewModel.onSaveIntervalWithUiFeedback(
						mapViewModel,
						keyboardController,
						coroutineScope,
						snackbarHostState
					)
				}, modifier = Modifier.fillMaxWidth()) {
					Text(UIConstants.BUTTON_SAVE_INTERVAL)
				}

				Spacer(modifier = Modifier.height(UIConstants.SPACER_LARGE))

				Button(
					onClick = {
						exportAndShareLocations(context, locations)
					},
					modifier = Modifier.fillMaxWidth()
				) {
					Text(UIConstants.BUTTON_EXPORT_JSON)
				}

				StatsSection(stats = stats)
			}
		}
	}
}
