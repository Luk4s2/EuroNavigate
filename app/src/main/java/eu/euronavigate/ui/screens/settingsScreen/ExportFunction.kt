package eu.euronavigate.ui.screens.settingsScreen

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import eu.euronavigate.data.model.LocationData
import eu.euronavigate.ui.utils.UIConstants
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun exportAndShareLocations(context: Context, locations: List<LocationData>) {
	val jsonString = Json.encodeToString(locations)
	val file = File(context.cacheDir, UIConstants.JSON_FILE_NAME)
	file.writeText(jsonString)

	val uri = FileProvider.getUriForFile(
		context,
		"${context.packageName}.provider",
		file
	)

	val intent = Intent(Intent.ACTION_SEND).apply {
		type = UIConstants.JSON_MIME_TYPE
		putExtra(Intent.EXTRA_STREAM, uri)
		putExtra(Intent.EXTRA_TITLE, UIConstants.JSON_SHARE_TITLE)
		addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
	}

	context.startActivity(Intent.createChooser(intent, UIConstants.JSON_SHARE_TITLE))
}
