package features

import kotlinx.serialization.Serializable

@Serializable
data class Node(val id: Long, val lat: Double, val lon: Double)
