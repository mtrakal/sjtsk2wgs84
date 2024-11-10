package cz.mtrakal.sjtsk2wgs84

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "S-JTSK to WGS84",
    ) {
        App()
    }
}