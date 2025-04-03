package eu.euronavigate.ui.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import eu.euronavigate.data.model.LocationDataModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedWriter
import java.io.File
import java.io.OutputStreamWriter

fun exportAndShareLocations(context: Context, locations: List<LocationDataModel>) {
	val file = File(context.cacheDir, UIConstants.JSON_FILE_NAME)

	file.outputStream().use { outputStream ->
		BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
			writer.write("[\n")
			val json = Json { prettyPrint = true }

			locations.forEachIndexed { index, location ->
				val itemJson = json.encodeToString(location)
				writer.write(itemJson)
				if (index != locations.lastIndex) writer.write(",\n")
			}

			writer.write("\n]")
		}
	}

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
