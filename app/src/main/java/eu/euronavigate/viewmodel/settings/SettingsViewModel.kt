package eu.euronavigate.viewmodel.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.euronavigate.domain.usecase.GetSavedIntervalUseCase
import eu.euronavigate.domain.usecase.SaveTrackingIntervalUseCase
import eu.euronavigate.ui.utils.UIConstants
import eu.euronavigate.viewmodel.map.MapViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	private val getSavedIntervalUseCase: GetSavedIntervalUseCase,
	private val saveTrackingIntervalUseCase: SaveTrackingIntervalUseCase
) : ViewModel() {

	private val _interval = mutableStateOf("")
	val interval: State<String> = _interval

	private val _isLoading = mutableStateOf(true)
	val isLoading: State<Boolean> = _isLoading

	fun initializeIntervalSettings() {
		viewModelScope.launch {
			val saved = getSavedIntervalUseCase()
			_interval.value = saved.toString()
			_isLoading.value = false
		}
	}

	fun updateIntervalInput(new: String) {
		_interval.value = new
	}

	fun onSaveIntervalWithUiFeedback(
		mapViewModel: MapViewModel,
		keyboardController: SoftwareKeyboardController?,
		scope: CoroutineScope,
		snackbarHostState: SnackbarHostState
	) {
		keyboardController?.hide()

		val intervalValue = interval.value.toLongOrNull() ?: UIConstants.FALLBACK_INTERVAL

		viewModelScope.launch {
			saveTrackingIntervalUseCase(intervalValue)
			_interval.value = intervalValue.toString()
			mapViewModel.updateTrackingInterval(intervalValue)
			mapViewModel.restartTrackingIfRunning()

			scope.launch {
				snackbarHostState.showSnackbar(UIConstants.LABEL_SNACKBAR_SAVING)
			}
		}
	}
}
