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
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val coroutineScope = rememberCoroutineScope()

        val launcher = rememberFilePickerLauncher(
            type = PickerType.File(extensions = listOf("gml")),
            mode = PickerMode.Single,
            title = "Pick a GML file",
        ) { file ->
            coroutineScope.launch {
                file?.path?.let { GmlReaderUseCase().invoke(it)}
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