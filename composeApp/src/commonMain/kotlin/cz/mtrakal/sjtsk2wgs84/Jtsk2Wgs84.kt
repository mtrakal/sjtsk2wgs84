package cz.mtrakal.sjtsk2wgs84

import kotlinx.serialization.Serializable
import kotlin.math.*


class Jtsk2Wgs84 {
    fun convert(jtsk: Jtsk): Wgs84 {
        var x = jtsk.coordinateX
        var y = jtsk.coordinateY

        if (x < 0 && y < 0) {
            x = -x
            y = -y
        }

        if (y > x) {
            val temp = x
            x = y
            y = temp
        }

        val a = 6377397.15508
        val e = 0.081696831215303
        val n = 0.97992470462083
        val konstURo = 12310230.12797036
        val sinUQ = 0.863499969506341
        val cosUQ = 0.504348889819882
        val sinVQ = 0.420215144586493
        val cosVQ = 0.907424504992097
        val alfa = 1.000597498371542
        val k = 1.003419163966575

        val ro = sqrt(x * x + y * y)
        val epsilon = 2 * atan(y / (ro + x))

        val D = epsilon / n

        val S = 2 * atan(exp(1 / n * ln(konstURo / ro))) - PI / 2

        val sinS = sin(S)
        val cosS = cos(S)
        val sinU = sinUQ * sinS - cosUQ * cosS * cos(D)
        val cosU = sqrt(1 - sinU * sinU)
        val sinDV = sin(D) * cosS / cosU
        val cosDV = sqrt(1 - sinDV * sinDV)
        val sinV = sinVQ * cosDV - cosVQ * sinDV
        val cosV = cosVQ * cosDV + sinVQ * sinDV
        val Ljtsk = 2 * atan(sinV / (1 + cosV)) / alfa
        var t = exp(2 / alfa * ln((1 + sinU) / cosU / k))
        var pom = (t - 1) / (t + 1)

        while (true) {
            val sinB = pom
            pom = t * exp(e * ln((1 + e * sinB) / (1 - e * sinB)))
            pom = (pom - 1) / (pom + 1)
            if (abs(pom - sinB) <= 1e-15) break
        }

        val Bjtsk = atan(pom / sqrt(1 - pom * pom))

        val a2 = 6377397.15508
        val f1 = 299.152812853
        val e2 = 1 - (1 - 1 / f1) * (1 - 1 / f1)
        val ro2 = a2 / sqrt(1 - e2 * sin(Bjtsk) * sin(Bjtsk))
        val x2 = (ro2 + jtsk.altitude) * cos(Bjtsk) * cos(Ljtsk)
        val y2 = (ro2 + jtsk.altitude) * cos(Bjtsk) * sin(Ljtsk)
        val z2 = ((1 - e2) * ro2 + jtsk.altitude) * sin(Bjtsk)

        val dx = 570.69
        val dy = 85.69
        val dz = 462.84
        val wz = -5.2611 / 3600 * PI / 180
        val wy = -1.58676 / 3600 * PI / 180
        val wx = -4.99821 / 3600 * PI / 180
        val m = 3.543e-6
        val xn = dx + (1 + m) * (x2 + wz * y2 - wy * z2)
        val yn = dy + (1 + m) * (-wz * x2 + y2 + wx * z2)
        val zn = dz + (1 + m) * (wy * x2 - wx * y2 + z2)

        val a3 = 6378137.0
        val f2 = 298.257223563
        val aB = f2 / (f2 - 1)
        val p = sqrt(xn * xn + yn * yn)
        val e22 = 1 - (1 - 1 / f2) * (1 - 1 / f2)
        val theta = atan(zn * aB / p)
        val st = sin(theta)
        val ct = cos(theta)
        t = (zn + e22 * aB * a3 * st * st * st) / (p - e22 * a3 * ct * ct * ct)
        val B = atan(t)
        val L = 2 * atan(yn / (p + xn))
        val hOut = sqrt(1 + t * t) * (p - a3 / sqrt(1 + (1 - e22) * t * t))

        val lat = B * 180 / PI
        val long = L * 180 / PI
        val height = floor(hOut * 100) / 100

        return Wgs84(lat, long, height)
    }
}

@Serializable
data class Jtsk(
    val coordinateX: Double,
    val coordinateY: Double,
    val altitude: Double,
)

@Serializable
data class Wgs84(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
)

@Serializable
data class GeoJson(
    val type: String = "FeatureCollection",
    val features: List<Feature>,
)

@Serializable
data class Feature(
    val type: String = "Feature",
    val geometry: Geometry,
    val properties: Properties,
)

@Serializable
data class Geometry(
    val type: String = "Point",
    val coordinates: List<Double>,
)

@Serializable
data class Properties(
    val altitude: String,
)

fun List<Wgs84>.toGeoJson(): GeoJson {
    return GeoJson(
        features = this.map {
            Feature(
                geometry = Geometry(
                    coordinates = listOf(it.longitude, it.latitude, it.altitude),
                ),
                properties = Properties(
                    altitude = it.altitude.toString(),
                ),
            )
        }
    )
}