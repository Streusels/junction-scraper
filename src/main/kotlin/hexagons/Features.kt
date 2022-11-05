package hexagons

import kotlinx.serialization.Serializable

@Serializable
data class Features(val type: String, val id: String, val geometry: Geometry)
