package model

data class ModelGraphHopper(
    val hits: List<Hit>,
    val took: Long
)

data class Hit(
    val city: String,
    val country: String,
    val extent: List<Double>,
    val housenumber: String,
    val name: String,
    val osm_id: Long,
    val osm_key: String,
    val osm_type: String,
    val osm_value: String,
    val point: GraphHopperPoint,
    val postcode: String,
    val street: String
)

data class GraphHopperPoint(
    val lng: Double,
    val lat: Double)
{
    fun point() = Point(lng, lat)
}

