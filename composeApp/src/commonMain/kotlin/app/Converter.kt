package app

interface Converter {
    fun convert(source: String = "epsg:25833", target: String = "epsg:4326", jtsk: Jtsk): Wgs84
}

expect fun getConverter(): Converter

