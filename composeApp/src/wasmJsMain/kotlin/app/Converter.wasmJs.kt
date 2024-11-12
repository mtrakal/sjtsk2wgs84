package app

class ConverterWasmJs : Converter {
    override fun convert(source: String, target: String, jtsk: Jtsk): Wgs84 {
        TODO("Not yet implemented")
    }
}

actual fun getConverter(): Converter = ConverterWasmJs()