package eu.euronavigate.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationData(
	val latitude: Double,
	val longitude: Double,
	val timestamp: Long,
	val accuracy: Float,
	val provider: String,
	val speed: Float,
	val altitude: Double? = null
)
