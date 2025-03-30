package eu.euronavigate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import eu.euronavigate.ui.navigation.Navigation
import eu.euronavigate.ui.theme.EuroNavigateTheme
import eu.euronavigate.viewmodel.MapViewModel

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val viewModel: MapViewModel by viewModels()

		setContent {
			EuroNavigateTheme {
				val navController = rememberNavController()
				Navigation(navController = navController, viewModel = viewModel)
			}
		}
	}
}