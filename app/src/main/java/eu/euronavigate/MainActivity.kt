package eu.euronavigate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import eu.euronavigate.ui.navigation.Navigation
import eu.euronavigate.ui.theme.EuroNavigateTheme


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			EuroNavigateTheme {
				val navController = rememberNavController()
				Navigation(navController = navController)
			}
		}
	}
}