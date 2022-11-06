package features

import hexagons.Coord
import hexagons.Hexagon
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

suspend fun calculateFeatureCount(client: HttpClient, feature: Feature, hexagons: List<Hexagon>): IntArray {
    val result = IntArray(hexagons.size)

    val featureString = feature.tags.joinToString("") { "node[$it](area.searchArea);" }
    val query = "[out:json];area[name~'Helsinki|Espoo|Vantaa'] -%3E .searchArea;($featureString);(._;>;);out;"

    val requestString = "https://fi.junction.wimbes.de/api/interpreter?data=$query"

    val response = client.get(requestString.replace(" ", "%20"))
    val body = response.body<FeatureResponse>()


    body.elements.forEach { node ->
        val coord = Coord(node.lon, node.lat)
        var minDist = Double.POSITIVE_INFINITY
        var hexId = 0
        for (i in hexagons.indices) {
            val dist = coord.distance(hexagons[i].center)
            if (dist < minDist) {
                minDist = dist
                hexId = i
            }
        }
        result[hexId]++
    }

    return result
}