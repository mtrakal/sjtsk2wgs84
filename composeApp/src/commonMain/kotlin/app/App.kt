package app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startPeriodicUpdates()
    }

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

        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                if (uiState.loading) {
                    viewModel.cancel()
                } else {
                    launcher.launch()
                }
            }) {
                if (uiState.loading) {
                    CircularProgressIndicator(color = MaterialTheme.colors.onPrimary)
                } else {
                    Text("Open file")
                }
            }

            uiState.loadingPercentage.forEach {
                if (it.value >= 99.5f) {
                    Text("Done")
                } else {
                    LinearProgressIndicator(
                        progress = it.value,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}