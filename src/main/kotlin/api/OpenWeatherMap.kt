package api

import model.Point

class OpenWeatherMap(
    private val apiKey: String = System.getenv("OPEN_WEATHER_KEY")
) {

    fun url(point: Point) =
        "https://api.openweathermap.org/data/2.5/weather?lat=${point.lat}&lon=${point.lon}&units=metric&appid=$apiKey"
}