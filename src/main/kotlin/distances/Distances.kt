package distances

import hexagons.Hexagon
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.io.File
import kotlin.math.ceil
import kotlin.system.measureTimeMillis

private fun getCoordString(hexagons: List<Hexagon>): String =
    hexagons.joinToString(";") { "${it.center.lat},${it.center.lon}" }

suspend fun calculateDistances(client: HttpClient, endpoint: DistanceEndpoint, outFile: File, hexagons: List<Hexagon>) {
    val distances = Array(hexagons.size) { DoubleArray(hexagons.size) }

    var request = 1
    var requestsNeeded = ceil(hexagons.size / 50.0).toInt()
    requestsNeeded *= requestsNeeded

    for (i in hexagons.indices step 50) {
        val sources = hexagons.subList(i, minOf(i + 50, hexagons.size))
        for (j in hexagons.indices step 50) {
            val time = measureTimeMillis {
                val targets = hexagons.subList(j, minOf(j + 50, hexagons.size))
                val distance = getDistance(client, endpoint, sources, targets)

                for (k in distance.indices) {
                    for (l in distance[0].indices) {
                        distances[i + k][j + l] = distance[k][l]
                    }
                }
            }
            println("Done with $request of $requestsNeeded; Took $time ms; Endpoint: $endpoint")
            request++
        }
    }

    val resultString = distances.joinToString("\n") { it.joinToString(" ") }
    outFile.writeText(resultString)
}


private suspend fun getDistance(
    client: HttpClient,
    endpoint: DistanceEndpoint,
    sources: List<Hexagon>,
    targets: List<Hexagon>
): List<List<Double>> {
    val requestString = endpoint.baseAddress +
            "${getCoordString(sources)};${getCoordString(targets)}" +
            "?sources=${sources.indices.joinToString(";")}" +
            "&destinations=${(sources.size until sources.size + targets.size).joinToString(";")}"

    val result: DistanceResponse = client.get(requestString).body()

    return result.durations
}