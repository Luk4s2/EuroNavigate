package eu.euronavigate.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import eu.euronavigate.ui.screens.mapScreen.MapScreen
import eu.euronavigate.ui.screens.settingsScreen.SettingsScreen
import eu.euronavigate.viewmodel.map.MapViewModel
import eu.euronavigate.viewmodel.settings.SettingsViewModel

@Composable
fun Navigation(navController: NavHostController) {
	val mapViewModel = hiltViewModel<MapViewModel>()
	val settingsViewModel = hiltViewModel<SettingsViewModel>()

	Scaffold { paddingValues ->
		NavHost(
			navController = navController,
			startDestination = "map",
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
		) {
			composable("map") {
				MapScreen(
					viewModel = mapViewModel,
					onNavigateToSettings = { navController.navigate("settings") }
				)
			}
			composable("settings") {
				SettingsScreen(
					viewModel = settingsViewModel,
					mapViewModel = mapViewModel,
					navController = navController
				)
			}
		}
	}
}