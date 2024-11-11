package app.koin

import androidx.lifecycle.viewmodel.compose.viewModel
import app.AppViewModel
import app.Platform
import app.getPlatform
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// Common App Definitions
fun appModule() = module {
    single<Platform> { getPlatform() }
    single<Json> {
        Json {
            prettyPrint = true
            encodeDefaults = true
        }
    }
    viewModelOf(::AppViewModel)
}