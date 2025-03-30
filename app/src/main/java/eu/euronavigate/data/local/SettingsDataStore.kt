package eu.euronavigate.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import eu.euronavigate.ui.utils.UIConstants
import kotlinx.coroutines.flow.first

object SettingsDataStore {
	private const val INTERVAL_KEY = "tracking_interval"
	private val Context.dataStore by preferencesDataStore("settings")

	suspend fun saveInterval(context: Context, interval: Long) {
		context.dataStore.edit { prefs ->
			prefs[longPreferencesKey(INTERVAL_KEY)] = interval
		}
	}

	suspend fun getInterval(context: Context): Long {
		val prefs = context.dataStore.data.first()
		return prefs[longPreferencesKey(INTERVAL_KEY)] ?: UIConstants.FALLBACK_INTERVAL
	}
}
