package app.koin

import app.*
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
    single<Converter> { getConverter() }
    viewModelOf(::AppViewModel)
}