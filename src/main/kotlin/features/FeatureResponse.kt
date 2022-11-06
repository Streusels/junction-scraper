package features

import kotlinx.serialization.Serializable

@Serializable
data class FeatureResponse(val elements: List<Node>)
