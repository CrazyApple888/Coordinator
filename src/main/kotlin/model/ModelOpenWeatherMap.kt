package model

data class ModelOpenWeatherMap(
    val base: String,
    val clouds: Clouds,
    val cod: Long,
    val coord: Point,
    val dt: Long,
    val id: Long,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Long,
    val visibility: Long,
    val weather: List<Weather>,
    val wind: Wind
)

data class Clouds(
    val all: Long
)

data class Main(
    val feels_like: Double,
    val humidity: Long,
    val pressure: Long,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
)

data class Sys(
    val country: String,
    val id: Long,
    val message: Double,
    val sunrise: Long,
    val sunset: Long,
    val type: Long
)

data class Weather(
    val description: String,
    val icon: String,
    val id: Long,
    val main: String
)

data class Wind(
    val deg: Long,
    val speed: Double
)
