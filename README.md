# How to?

Add your API keys to environment variables:
- GRAPH_HOPPER_KEY
- OPEN_TRIP_MAP_KEY
- OPEN_WEATHER_KEY

# Or
Pass keys as parameters for DataProvider's constructor: </br>
DataProvider                </br>
(                           </br>
HTTPClient(),               </br>
GraphHopper( << key >> ),   </br>
OpenTripMap( << key >> ),   </br>
OpenWeatherMap( << key >> ) </br>
)
