package eu.euronavigate.data.model

data class LocationUIModel(
	val latitude: String,
	val longitude: String,
	val accuracy: String,
	val time: String,
	val provider: String
)