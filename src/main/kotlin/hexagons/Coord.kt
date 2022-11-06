package hexagons

import kotlinx.serialization.Serializable
import kotlin.math.sqrt

@Serializable
data class Coord(val lat: Double, val lon: Double) {

    fun midpoint(other: Coord): Coord {
        return Coord((this.lat + other.lat) / 2, (this.lon + other.lon) / 2)
    }

    fun distance(other: Coord): Double {
        return sqrt((this.lat - other.lat) * (this.lat - other.lat) + (this.lon - other.lon) * (this.lon - other.lon))
    }
}
