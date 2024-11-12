package app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.usecase.GmlReaderUseCase
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AppViewModel(
    private val json: Json,
    private val converter: Converter
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState.initial())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AppUiEvent>()
    val uiEvent: SharedFlow<AppUiEvent> = _uiEvent.asSharedFlow()

    fun x(file: PlatformFile) {
        viewModelScope.launch {
            file.path?.let {
                val jtsks = GmlReaderUseCase().invoke(it)
//                val converter = Jtsk2Wgs84()

                val wgs84s: List<Wgs84> = jtsks.map {
                    converter.convert(jtsk = it)
                }

                val geojson = wgs84s.toGeoJson()

                _uiEvent.emit(
                    AppUiEvent.ShowSaveDialog(
                        file.name.substringBeforeLast("."),
                        json.encodeToString(geojson).encodeToByteArray()
                    )
                )
            }
        }
    }
}

data class AppUiState(
    val loading: Boolean,
) {
    companion object {
        fun initial() = AppUiState(loading = false)
    }
}

sealed class AppUiEvent {
    data class ShowSaveDialog(
        val filename: String,
        val bytes: ByteArray
    ) : AppUiEvent()
}