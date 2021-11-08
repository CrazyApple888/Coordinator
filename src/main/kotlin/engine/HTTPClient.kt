package engine

import util.Loggable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.observer.*
import io.ktor.client.request.*
import kotlinx.coroutines.*


class HTTPClient : Loggable {
    val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = GsonSerializer {
                setPrettyPrinting()
            }
        }
        ResponseObserver { response ->
            logger.info("HTTP status: ${response.status.value} for $response")
        }
    }

    inline fun <reified T> getAsync(url: String) : Deferred<T> = CoroutineScope(Dispatchers.IO).async {
        client.get(url)
    }

}