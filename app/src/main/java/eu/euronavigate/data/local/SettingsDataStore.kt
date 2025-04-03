package eu.euronavigate.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.euronavigate.ui.utils.UIConstants
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings")

@Singleton
class SettingsDataStore @Inject constructor(
	@ApplicationContext private val context: Context
) {
	companion object {
		private val INTERVAL_KEY = longPreferencesKey("tracking_interval")
	}

	suspend fun saveInterval(interval: Long) {
		context.dataStore.edit { prefs ->
			prefs[INTERVAL_KEY] = interval
		}
	}

	suspend fun getInterval(): Long {
		val prefs = context.dataStore.data.first()
		return prefs[INTERVAL_KEY] ?: UIConstants.FALLBACK_INTERVAL
	}
}
