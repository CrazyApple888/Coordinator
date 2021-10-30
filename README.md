# How to?

Add your API keys to PATH:
- GRAPH_HOPPER_KEY
- OPEN_TRIP_MAP_KEY
- OPEN_WEATHER_KEY

# Or
Pass keys as parameters for DataProvider's constructor:
DataProvider                </br>
(                           </br>
HTTPClient( << key >> ),    </br>
GraphHopper( << key >> ),   </br>
OpenTripMap( << key >> ),   </br>
OpenWeatherMap( << key >> ) </br>
)