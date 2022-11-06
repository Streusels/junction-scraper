import distances.DistanceEndpoint
import distances.calculateDistances
import features.Feature
import features.calculateFeatureCount
import hexagons.Hexagon
import hexagons.Hexagons
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File

suspend fun main() {
    val inFile = File("data/helsinki_hexagons.json")
    val hexagons = Hexagons.getFromDataFile(inFile)

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 5)
                exponentialDelay()
            }
        }
    }

//    calculateAllDistances(client, hexagons)
    calculateFeatureCounts(client, hexagons)
}

suspend fun calculateAllDistances(client: HttpClient, hexagons: List<Hexagon>) {
    val outCar = File("data/helsinki_car_dists.txt")
    val bikeCar = File("data/helsinki_bike_dists.txt")
    val footCar = File("data/helsinki_foot_dists.txt")

    val jobs = ArrayList<Job>(3)

    jobs.add(CoroutineScope(Dispatchers.IO).launch {
        calculateDistances(client, DistanceEndpoint.CAR, outCar, hexagons)
    })
    jobs.add(CoroutineScope(Dispatchers.IO).launch {
        calculateDistances(client, DistanceEndpoint.BIKE, bikeCar, hexagons)
    })
    jobs.add(CoroutineScope(Dispatchers.IO).launch {
        calculateDistances(client, DistanceEndpoint.FOOT, footCar, hexagons)
    })

    jobs.forEach { it.join() }
}

suspend fun calculateFeatureCounts(client: HttpClient, hexagons: List<Hexagon>) {
    val outFile = File("data/helsinki_features.txt")

    val result = Array(hexagons.size) { IntArray(Feature.values().size) }

    val jobs = ArrayList<Job>(Feature.values().size)

    for (f in Feature.values().indices) {
        val feature = Feature.values()[f]
        jobs.add(CoroutineScope(Dispatchers.IO).launch {
            val counts = calculateFeatureCount(client, feature, hexagons)
            for (i in counts.indices) {
                result[i][f] = counts[i]
            }
        })
    }

    jobs.forEach { it.join() }

    val resultString = result.joinToString("\n") { it.joinToString(" ") }
    outFile.writeText(resultString)
}