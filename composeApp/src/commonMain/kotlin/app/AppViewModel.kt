package app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.usecase.CpuCoresUseCase
import app.usecase.GmlReaderUseCase
import co.touchlab.stately.collections.ConcurrentMutableMap
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AppViewModel(
    private val json: Json,
    private val converter: Converter,
    private val cpuCores: CpuCoresUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState.initial())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AppUiEvent>()
    val uiEvent: SharedFlow<AppUiEvent> = _uiEvent.asSharedFlow()

    private var loadingPercentage: ConcurrentMutableMap<Int, Float> = ConcurrentMutableMap()
    private var job: Job? = null

    private fun updateState() {
        _uiState.value = _uiState.value.copy(
            loading = job.let { it != null && it.isActive },
            loadingPercentage = loadingPercentage.toMap()
        )
    }

    fun x(file: PlatformFile) {
        job = viewModelScope.launch(Dispatchers.Default) {
            updateState()
            file.path?.let {
                val jtsks = GmlReaderUseCase().invoke(it)
//                val converter = Jtsk2Wgs84()

                val cpuCores: Int = cpuCores()
                val chunkedJtsks = jtsks.chunked(jtsks.size / cpuCores + 1)

                val wgs84s: List<Wgs84> = chunkedJtsks.mapIndexed { index, chunk ->
                    async {
                        loadingPercentage[index] = 0f
                        val percentage = 100f / chunk.size
                        chunk.map {
                            ensureActive()
                            loadingPercentage[index] = loadingPercentage[index]!! + percentage
                            converter.convert(jtsk = it)
                        }
                    }
                }.awaitAll().flatten().filter {
                    isWithinBounds(
                        it,
                        Point(50.6226469, 15.2661303),
                        Point(50.6222956, 15.2668211)
                    )
                }

                val geojson = wgs84s.toGeoJson()

                _uiEvent.emit(
                    AppUiEvent.ShowSaveDialog(
                        file.name.substringBeforeLast("."),
                        json.encodeToString(geojson).encodeToByteArray()
                    )
                )
            }
        }.apply {
            start()
        }
    }

    fun isWithinBounds(
        point: Wgs84,
        topLeft: Point,
        bottomRight: Point,
    ): Boolean {
        return point.latitude in bottomRight.latitude..topLeft.latitude &&
                point.longitude in topLeft.longitude..bottomRight.longitude
    }

    fun startPeriodicUpdates() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                updateState()
            }
        }
    }

    fun cancel() {
        job?.cancel()
        loadingPercentage = ConcurrentMutableMap()
        updateState()
    }
}

data class AppUiState(
    val loading: Boolean = false,
    val loadingPercentage: Map<Int, Float> = emptyMap(),
) {
    companion object {
        fun initial() = AppUiState()
    }
}

sealed class AppUiEvent {
    data class ShowSaveDialog(
        val filename: String,
        val bytes: ByteArray
    ) : AppUiEvent()
}