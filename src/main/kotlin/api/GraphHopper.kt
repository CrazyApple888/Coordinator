package api

class GraphHopper(
    private val apiKey: String = System.getenv("GRAPH_HOPPER_KEY")
) {

    fun url(place: String): String {
        return "https://graphhopper.com/api/1/geocode?q=$place&locale=en&key=$apiKey"
    }
}