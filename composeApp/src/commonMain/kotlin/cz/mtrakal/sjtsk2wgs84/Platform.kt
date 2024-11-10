package cz.mtrakal.sjtsk2wgs84

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform