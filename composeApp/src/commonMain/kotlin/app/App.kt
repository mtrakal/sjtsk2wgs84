package app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    viewModel: AppViewModel = koinViewModel()
) {
    MaterialTheme {
        // FileKit Compose
        val launcherSave = rememberFileSaverLauncher() { file ->
            // Handle the saved file
        }


        LaunchedEffect(key1 = "vmEvent") {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is AppUiEvent.ShowSaveDialog -> {
                        launcherSave.launch(
                            baseName = event.filename,
                            extension = "geojson",
                            bytes = event.bytes
                        )
                    }
                }
            }
        }

        val launcher = rememberFilePickerLauncher(
            type = PickerType.File(extensions = listOf("gml")),
            mode = PickerMode.Single,
            title = "Pick a GML file",
        ) { file ->
            file?.let { viewModel.x(it) }
        }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                launcher.launch()
            }) {
                Text("Open file")
            }
        }
    }
}