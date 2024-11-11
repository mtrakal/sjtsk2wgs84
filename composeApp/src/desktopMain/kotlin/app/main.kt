package app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.koin.initKoin

fun main() = application {
    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "S-JTSK to WGS84",
    ) {
        App()
    }
}