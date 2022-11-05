package hexagons

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.io.FileInputStream

@Serializable
data class Hexagons(val type: String, val features: List<Features>) {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        fun getFromDataFile(file: File): List<Hexagon> {
            val input = Json.decodeFromStream<Hexagons>(FileInputStream(file))
            val result = ArrayList<Hexagon>(input.features.size)

            for (feature in 0 until input.features.size) {
                val oldCoords = input.features[feature].geometry.coordinates.first()

                val coords = ArrayList<Coord>(6)
                for (coord in 0 until 6) {
                    val lat = oldCoords[coord][0]
                    val lon = oldCoords[coord][1]
                    coords.add(Coord(lat, lon))
                }

                val center = coords[0].midpoint(coords[3])

                result.add(Hexagon(coords, center))
            }

            return result
        }
    }
}
