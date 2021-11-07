package engine

import util.Loggable
import api.GraphHopper
import api.OpenTripMap
import api.OpenWeatherMap
import model.*
import model.otm.OtmDescription

class DataProvider(
    private val httpClient: HTTPClient,
    private val ghApi: GraphHopper = GraphHopper(),
    private val otmApi: OpenTripMap = OpenTripMap(),
    private val owmApi: OpenWeatherMap = OpenWeatherMap()
) : Loggable {

    suspend fun requestPlaces(place: String): List<Hit> {
        logger.info("Requested places near $place")
        return httpClient.getAsync<ModelGraphHopper>(ghApi.placesAroundUrl(place)).await().hits
    }

    suspend fun requestTemp(point: Point): String {
        logger.info("Requested temperature for $point")
        return httpClient.getAsync<ModelOpenWeatherMap>(owmApi.weatherByPointUrl(point)).await().main.temp.toString()
    }

    suspend fun requestPlacesByRadius(point: Point, radius: Int = 1000): List<OpenTripMapPlacesModelItem> {
        logger.info("Requested places in $radius around $point")
        return httpClient.getAsync<ModelOpenTripMapPlaces>(otmApi.listOfPlacesByRadiusUrl(point, radius)).await()
    }

    suspend fun requestDescription(place: OpenTripMapPlacesModelItem): String {
        logger.info("Requested description for $place")
        return httpClient.getAsync<OtmDescription>(otmApi.placeByXidUrl(place.xid)).await().wikipedia_extracts?.text
            ?: "There is not description for this place"
    }
}