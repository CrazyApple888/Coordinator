import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.application
import engine.DataProvider
import engine.HTTPClient
import ui.Application

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    application {
        Application(DataProvider(HTTPClient())).Main()
    }
}
