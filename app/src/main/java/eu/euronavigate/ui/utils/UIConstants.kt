package eu.euronavigate.ui.utils

import androidx.compose.ui.unit.dp

object UIConstants {
	const val DEFAULT_ZOOM = 17f
	const val CAMERA_PADDING = 100

	// Padding & spacing
	val PADDING = 16.dp
	val SPACER_MINI = 4.dp
	val SPACER_SMALL = 8.dp
	val SPACER_MEDIUM = 16.dp
	val SPACER_LARGE = 24.dp

	// Button labels
	const val BUTTON_START_TRACKING = "Start Tracking"
	const val BUTTON_STOP_TRACKING = "Stop Tracking"
	const val BUTTON_GO_TO_SETTINGS = "Go to Settings"
	const val BUTTON_FIT_PINS = "Fit Pins"

	// File constants
	const val JSON_FILE_NAME = "locations.json"
	const val JSON_MIME_TYPE = "application/json"
	const val JSON_SHARE_TITLE = "Share JSON"

	// Tracking
	const val FALLBACK_INTERVAL = 5L
	const val LABEL_TRACKING_INTERVAL = "Tracking Interval (min)"

	// UI Labels
	const val LABEL_SETTINGS_TITLE = "Settings"
	const val LABEL_BACK = "Back"
	const val LABEL_SNACKBAR_SAVING = "Saving..."

	// Buttons
	const val BUTTON_SAVE_INTERVAL = "Save"
	const val BUTTON_EXPORT_JSON = "Export & Share JSON"
}
