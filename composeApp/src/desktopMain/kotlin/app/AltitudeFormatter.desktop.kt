package app

import java.math.BigDecimal
import java.math.RoundingMode

class AltitudeFormatterImpl : AltitudeFormatter {
    override fun convert(altitude: Double, decimalPlaces: Int): Double {
        return BigDecimal.valueOf(altitude).setScale(decimalPlaces, RoundingMode.HALF_EVEN).toDouble()
    }
}

actual fun getAltitudeFormatter(): AltitudeFormatter = AltitudeFormatterImpl()