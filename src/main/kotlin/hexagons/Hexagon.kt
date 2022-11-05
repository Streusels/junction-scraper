package hexagons

import kotlinx.serialization.Serializable

@Serializable
data class Hexagon(val coords: List<Coord>, val center: Coord)
