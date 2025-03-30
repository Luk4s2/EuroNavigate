package eu.euronavigate.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import eu.euronavigate.ui.screens.mapScreen.MapScreen
import eu.euronavigate.ui.screens.settingsScreen.SettingsScreen
import eu.euronavigate.viewmodel.MapViewModel

@Composable
fun Navigation(navController: NavHostController, viewModel: MapViewModel) {
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
					viewModel = viewModel,
					onNavigateToSettings = {
						navController.navigate("settings")
					}
				)
			}
			composable("settings") {
				SettingsScreen(viewModel = viewModel, navController = navController)
			}
		}
	}
}
