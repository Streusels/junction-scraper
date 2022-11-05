package hexagons

import kotlinx.serialization.Serializable

@Serializable
data class Coord(val lat: Double, val lon: Double) {

    fun midpoint(other: Coord): Coord {
        return Coord((this.lat + other.lat) / 2, (this.lon + other.lon) / 2)
    }
}
