package distances

enum class DistanceEndpoint(val baseAddress: String) {
    CAR("https://car.fi.junction.wimbes.de/table/v1/driving/"),
    BIKE("https://bike.fi.junction.wimbes.de/table/v1/bike/"),
    FOOT("https://foot.fi.junction.wimbes.de/table/v1/foot/")
}
