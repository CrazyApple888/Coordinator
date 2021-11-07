package ui

import engine.DataProvider
import util.Loggable
import androidx.compose.desktop.Window
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import model.Hit
import model.OpenTripMapPlacesModelItem

const val DEFAULT_WIDTH = 800
const val DEFAULT_HEIGHT = 600

class Application(
    private val provider: DataProvider
) : Loggable {

    private val scope = MainScope()

    @Composable
    fun Main() = Window(
        title = "Coordinator",
        size = IntSize(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    ) {
        val places = mutableStateListOf<Hit>()
        val placesAround = remember { mutableStateListOf<OpenTripMapPlacesModelItem>() }
        val weather = remember { mutableStateOf("") }

        MaterialTheme {
            Column(Modifier.fillMaxSize()) {
                SearchView(places, weather, placesAround)
                Content(places, weather, placesAround)
            }
        }
    }

    @Composable
    private fun SearchView(
        places: SnapshotStateList<Hit>,
        weather: MutableState<String>,
        placesAround: SnapshotStateList<OpenTripMapPlacesModelItem>
    ) {
        val buttonText = "Go!"
        var inputText by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }
        var hint by remember { mutableStateOf("Enter place") }
        var loading by remember { mutableStateOf(false) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                singleLine = true,
                value = inputText,
                onValueChange = {
                    inputText = it
                    if (isError) {
                        isError = false
                        hint = "Enter place"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(Dp(5f))
                    .align(Alignment.CenterHorizontally),
                label = { Text(hint) },
                isError = isError
            )
            Button(
                onClick = {
                    logger.info("Input: $inputText")
                    loading = true
                    scope.launch {
                        try {
                            weather.value = ""
                            places.clear()
                            placesAround.clear()
                            places.addAll(provider.requestPlaces(inputText))
                        } catch (th: Throwable) {
                            isError = true
                            logger.info("Get by place error: ${th.message}")
                        } finally {
                            loading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(buttonText)
            }
            if (loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(5.dp)
                )
            }
        }
    }

    @Composable
    private fun Content(
        places: SnapshotStateList<Hit>,
        weather: MutableState<String>,
        placesAround: SnapshotStateList<OpenTripMapPlacesModelItem>
    ) {
        Row {
            SearchResults(places, weather, placesAround)
            Column {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    //weather
                    Text(
                        text = if (weather.value.isEmpty()) "" else "Temperature ${weather.value} °C",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(5.dp)
                    )
                }
                //places and descriptions
                PlacesWithDescription(placesAround)
            }
        }
    }

    @Composable
    private fun PlacesWithDescription(placesAround: SnapshotStateList<OpenTripMapPlacesModelItem>) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = placesAround,
                itemContent = {
                    ItemPlaceWithDescription(it)
                }
            )
        }
    }

    @Composable
    private fun ItemPlaceWithDescription(place: OpenTripMapPlacesModelItem) {
        Column {
            val placeName = remember { place.name }
            val description = remember { mutableStateOf("") }
            val isButtonVisible = remember { mutableStateOf(true) }
            Text(
                text = placeName.ifBlank { "Place without name" },
                style = MaterialTheme.typography.h6
            )
            Text(
                text = description.value,
                style = MaterialTheme.typography.caption
            )
            Button(
                onClick = {
                    scope.launch {
                        description.value = provider.requestDescription(place)
                        isButtonVisible.value = false
                    }
                },
                enabled = isButtonVisible.value
            ) {
                Text("Get description")
            }
        }
    }

    @Composable
    private fun SearchResults(
        places: SnapshotStateList<Hit>,
        weather: MutableState<String>,
        placesAround: SnapshotStateList<OpenTripMapPlacesModelItem>
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.3F).fillMaxHeight(),
            content = {
                PlacesContent(places, weather, placesAround)
            }
        )
    }

    @Composable
    private fun PlacesContent(
        places: SnapshotStateList<Hit>,
        weather: MutableState<String>,
        placesAround: SnapshotStateList<OpenTripMapPlacesModelItem>
    ) {
        LazyColumn {
            items(
                items = places,
                itemContent = {
                    ItemPlace(it, weather, placesAround)
                }
            )
        }
    }

    @Composable
    private fun ItemPlace(
        place: Hit,
        weather: MutableState<String>,
        placesAround: SnapshotStateList<OpenTripMapPlacesModelItem>
    ) {
        Card(
            modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp)
                .fillMaxWidth()
                .clickable {
                    weather.value = ""
                    scope.launch { weather.value = provider.requestTemp(place.point.point()) }
                    scope.launch {
                        placesAround.clear()
                        placesAround.addAll(
                            provider.requestPlacesByRadius(place.point.point())
                        )
                    }
                    logger.info("WEATHER AND PLACES LAUNCHED")
                },
            elevation = 2.dp,
            backgroundColor = Color.White,
            shape = RoundedCornerShape(corner = CornerSize(3.dp))
        ) {
            Column {
                Text(place.name, style = MaterialTheme.typography.h6)
                Text(place.country, style = MaterialTheme.typography.caption)
                Text("lng = ${place.point.lng}, lat = ${place.point.lat}")
            }
        }
    }

}