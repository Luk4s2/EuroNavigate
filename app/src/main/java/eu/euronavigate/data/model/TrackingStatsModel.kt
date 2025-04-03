package eu.euronavigate.data.model

data class TrackingStatsModel(
	val distance: Double,
	val avgSpeed: Double?,
	val minSpeed: Float?,
	val maxSpeed: Float?,
	val altitude: Double?
)