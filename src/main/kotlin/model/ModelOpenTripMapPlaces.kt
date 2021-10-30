package model

class ModelOpenTripMapPlaces : ArrayList<OpenTripMapPlacesModelItem>()

data class OpenTripMapPlacesModelItem(
    val kind: String,
    val name: String,
    val osm: String,
    val point: Point,
    val wikidata: String,
    val xid: String
)