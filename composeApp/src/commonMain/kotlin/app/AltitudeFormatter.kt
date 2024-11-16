package app

interface AltitudeFormatter {
    fun convert(altitude: Double, decimalPlaces: Int = 2): Double
}

expect fun getAltitudeFormatter(): AltitudeFormatter

