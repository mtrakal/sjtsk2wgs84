package cz.mtrakal.sjtsk2wgs84

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val coroutineScope = rememberCoroutineScope()

        // FileKit Compose
        val launcherSave = rememberFileSaverLauncher() { file ->
            // Handle the saved file
        }

        val launcher = rememberFilePickerLauncher(
            type = PickerType.File(extensions = listOf("gml")),
            mode = PickerMode.Single,
            title = "Pick a GML file",
        ) { file ->
            coroutineScope.launch {
                file?.path?.let {
                    val jtsks = GmlReaderUseCase().invoke(it)
                    val converter = Jtsk2Wgs84()

                    val wgs84s: List<Wgs84> = jtsks.map {
                        converter.convert(it)
                    }

                    val geojson = wgs84s.toGeoJson()

                    launcherSave.launch(
                        baseName = "myTextFile",
                        extension = "txt",
                        bytes = Json {
                            prettyPrint = true
                            encodeDefaults = true
                        }.encodeToString(geojson).encodeToByteArray()
                    )
                }
            }
        }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                launcher.launch()
            }) {
                Text("Click me!")
            }
        }
    }
}