package distances

import kotlinx.serialization.Serializable

@Serializable
data class DistanceResponse(val code: String, val durations: List<List<Double>>)
