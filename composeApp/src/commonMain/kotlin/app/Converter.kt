package app

interface Converter {
    fun convert(jtsk: Jtsk): Wgs84
}

expect fun getConverter(source: String, target: String): Converter

