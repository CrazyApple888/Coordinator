package api

import model.Point

class OpenTripMap(
    private val apiKey: String = System.getenv("OPEN_TRIP_MAP_KEY")
) {

    fun placeByXidUrl(xid: String, lang: String = "en") : String {
        return "https://api.opentripmap.com/0.1/$lang/places/xid/$xid?apikey=$apiKey"
    }

    fun listOfPlacesByRadiusUrl(point: Point, radius: Int = 1000, limit: Int = 10, lang: String = "ru") : String {
        return "https://api.opentripmap.com/0.1/$lang/places/radius?radius=$radius&lon=${point.lon}&lat=${point.lat}&format=json&limit=$limit&apikey=$apiKey"
    }

}