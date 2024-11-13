package app

class ConverterWasmJs(source: String, target: String) : Converter {
    override fun convert(jtsk: Jtsk): Wgs84 {
        TODO("Not yet implemented")
    }
}

actual fun getConverter(source: String, target: String): Converter = ConverterWasmJs(source, target)