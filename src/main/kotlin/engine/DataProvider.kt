package engine

import util.Loggable
import api.GraphHopper
import api.OpenTripMap
import api.OpenWeatherMap
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import model.*
import model.otm.OtmDescription

class DataProvider(
    private val httpClient: HTTPClient,
    private val ghApi: GraphHopper = GraphHopper(),
    private val otmApi: OpenTripMap = OpenTripMap(),
    private val owmApi: OpenWeatherMap = OpenWeatherMap()
) : Loggable {

    suspend fun requestPlaces(place: String): List<Hit> {
        return httpClient.getAsync<ModelGraphHopper>(ghApi.url(place)).await().hits
    }

    suspend fun requestTemp(point: Point): String {
        return httpClient.getAsync<ModelOpenWeatherMap>(owmApi.url(point)).await().main.temp.toString()
    }

    suspend fun requestPlacesWithDescription(point: Point, radius: Int = 1000): SharedFlow<Pair<String, String>> {
        val places = httpClient.getAsync<ModelOpenTripMapPlaces>(otmApi.listOfPlacesByRadius(point, radius)).await()
        val descriptions = MutableSharedFlow<Pair<String, String>>()
        places.forEach {
            MainScope().launch {
                logger.info("SENDING REQUEST FOR ${it.xid}")
                descriptions.emit(
                    it.name to
                            (httpClient.getAsync<OtmDescription>(otmApi.placeByXidUrl(it.xid)).await().wikipedia_extracts?.text
                                ?: "")
                )
                logger.info("ANSWER FOR ${it.xid} EMITTED")
            }
            logger.info("AFTER COROUTINE SCOPE")
        }

        return descriptions.asSharedFlow()
    }
}