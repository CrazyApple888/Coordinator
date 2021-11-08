package model.otm

data class OtmDescription(
    val address: Address,
    val bbox: Bbox,
    val image: String,
    val kinds: String,
    val name: String,
    val osm: String,
    val otm: String,
    val point: Point,
    val preview: Preview,
    val rate: String,
    val sources: Sources,
    val wikidata: String,
    val wikipedia: String,
    val wikipedia_extracts: WikipediaExtracts?,
    val xid: String
)