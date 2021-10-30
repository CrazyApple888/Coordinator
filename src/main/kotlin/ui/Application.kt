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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import model.Hit

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
        val placesWithDescription = remember { mutableStateListOf<Pair<String, String>>() }
        val weather = remember { mutableStateOf("") }

        MaterialTheme {
            Column(Modifier.fillMaxSize()) {
                SearchView(places, placesWithDescription, weather)
                Content(places, placesWithDescription, weather)
            }
        }
    }

    @Composable
    private fun SearchView(
        places: SnapshotStateList<Hit>,
        placesWithDescription: SnapshotStateList<Pair<String, String>>,
        weather: MutableState<String>
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
                            placesWithDescription.clear()
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
    fun Content(
        places: SnapshotStateList<Hit>,
        placesWithDescription: SnapshotStateList<Pair<String, String>>,
        weather: MutableState<String>
    ) {
        Row {
            SearchResults(places, weather, placesWithDescription)
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
                PlacesWithDescription(placesWithDescription)
            }
        }
    }

    @Composable
    fun PlacesWithDescription(content: SnapshotStateList<Pair<String, String>>) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = content,
                itemContent = {
                    ItemPlaceWithDescription(it)
                }
            )
        }
    }

    @Composable
    fun ItemPlaceWithDescription(item: Pair<String, String>) {
        Column {
            Text(
                text = item.first,
                style = MaterialTheme.typography.h6
            )
            Text(
                text = item.second,
                style = MaterialTheme.typography.caption
            )
        }
    }

    @Composable
    fun SearchResults(
        places: SnapshotStateList<Hit>,
        weather: MutableState<String>,
        placesWithDescription: SnapshotStateList<Pair<String, String>>
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.3F).fillMaxHeight(),
            content = {
                PlacesContent(places, weather, placesWithDescription)
            }
        )
    }

    @Composable
    fun PlacesContent(
        places: SnapshotStateList<Hit>,
        weather: MutableState<String>,
        placesWithDescription: SnapshotStateList<Pair<String, String>>
    ) {
        LazyColumn {
            items(
                items = places,
                itemContent = {
                    ItemPlace(it, weather, placesWithDescription)
                }
            )
        }
    }

    @Composable
    fun ItemPlace(
        place: Hit,
        weather: MutableState<String>,
        placesWithDescription: SnapshotStateList<Pair<String, String>>
    ) {
        Card(
            modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp)
                .fillMaxWidth()
                .clickable {
                    weather.value = ""
                    placesWithDescription.clear()
                    scope.launch { weather.value = provider.requestTemp(place.point.point()) }
                    scope.launch {
                        provider.requestPlacesWithDescription(place.point.point())
                            .collect {
                                placesWithDescription.add(it)
                                logger.info("COLLECTOR: $it")
                            }
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