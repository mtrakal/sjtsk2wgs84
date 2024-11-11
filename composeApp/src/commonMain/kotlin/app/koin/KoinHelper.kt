package app.koin

import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication

fun initKoin(){
    startKoin {
        modules(appModule())
    }
}

fun koinConfiguration() = koinApplication {
    // your configuration & modules here
    modules(appModule())
}