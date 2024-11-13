package app

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.*


class Jtsk2Wgs84 {

    /**
     * https://github.com/arodax/jtsk2wgs84/blob/master/src/jtsk2wgs84.js
     */
    fun arodax(jtsk: Jtsk): Wgs84 {
        val a = 6377397.15508
        val e = 0.081696831215303
        val n = 0.97992470462083
        val uRo = 12310230.12797036
        val sinUQ = 0.863499969506341
        val cosUQ = 0.504348889819882
        val sinVQ = 0.420215144586493
        val cosVQ = 0.907424504992097
        val alpha = 1.000597498371542
        val k = 1.003419163966575

        var ro = sqrt(jtsk.coordinateX * jtsk.coordinateX + jtsk.coordinateY * jtsk.coordinateY)
        val epsilon = 2 * atan(jtsk.coordinateY / (ro + jtsk.coordinateX))
        val D = epsilon / n
        val S = 2 * atan(exp(1 / n * ln(uRo / ro))) - PI / 2
        val sinS = sin(S)
        val cosS = cos(S)
        val sinU = sinUQ * sinS - cosUQ * cosS * cos(D)
        val cosU = sqrt(1 - sinU * sinU)
        val sinDV = sin(D) * cosS / cosU
        val cosDV = sqrt(1 - sinDV * sinDV)
        val sinV = sinVQ * cosDV - cosVQ * sinDV
        val cosV = cosVQ * cosDV + sinVQ * sinDV
        val lJtsk = 2 * atan(sinV / (1 + cosV)) / alpha
        var t = exp(2 / alpha * ln((1 + sinU) / cosU / k))
        var pom = (t - 1) / (t + 1)

        var sinB: Double

        do {
            sinB = pom
            pom = t * exp(e * ln((1 + e * sinB) / (1 - e * sinB)))
            pom = (pom - 1) / (pom + 1)
        } while (abs(pom - sinB) > 1e-15)

        val bJtsk = atan(pom / sqrt(1 - pom * pom))

        val f1 = 299.152812853
        val e2 = 1 - (1 - 1 / f1) * (1 - 1 / f1)
        ro = a / sqrt(1 - e2 * sin(bJtsk) * sin(bJtsk))
        var x = (ro + jtsk.altitude) * cos(bJtsk) * cos(lJtsk)
        var y = (ro + jtsk.altitude) * cos(bJtsk) * sin(lJtsk)
        val z = ((1 - e2) * ro + jtsk.altitude) * sin(bJtsk)

        val dx = 570.69
        val dy = 85.69
        val dz = 462.84
        val wz = -5.2611 / 3600 * PI / 180
        val wy = -1.58676 / 3600 * PI / 180
        val wx = -4.99821 / 3600 * PI / 180
        val m = 3.543e-6
        val xn = dx + (1 + m) * (x + wz * y - wy * z)
        val yn = dy + (1 + m) * (-wz * x + y + wx * z)
        val zn = dz + (1 + m) * (wy * x - wx * y + z)

        val a2 = 6378137.0
        val f2 = 298.257223563
        val aB = f2 / (f2 - 1)
        val p = sqrt(xn * xn + yn * yn)
        val e22 = 1 - (1 - 1 / f2) * (1 - 1 / f2)
        val theta = atan(zn * aB / p)
        val st = sin(theta)
        val ct = cos(theta)
        t = (zn + e22 * aB * a2 * st * st * st) / (p - e22 * a2 * ct * ct * ct)
        var B = atan(t)
        var L = 2 * atan(yn / (p + xn))
        val hOut = sqrt(1 + t * t) * (p - a2 / sqrt(1 + (1 - e22) * t * t))

        B = B / PI * 180
        val coordsLat = B

        var latitude = 'N'
        if (B < 0) {
            B = -B
            latitude = 'S'
        }

        val latDeg = floor(B).toInt()
        B = (B - latDeg) * 60
        val latMin = floor(B).toInt()
        B = (B - latMin) * 60
        val latSec = round(B * 1000) / 1000
        val wgs84Lat = "$latDeg°$latMin'$latSec$latitude"

        L = L / PI * 180
        val coordsLon = L
        var longitude = 'E'
        if (L < 0) {
            L = -L
            longitude = 'W'
        }

        val lonDeg = floor(L).toInt()
        L = (L - lonDeg) * 60
        val lonMin = floor(L).toInt()
        L = (L - lonMin) * 60
        val lonSec = round(L * 1000) / 1000

        val wgs84Altitude = round(hOut * 100) / 100

        return Wgs84(coordsLat, coordsLon, wgs84Altitude, jtsk)
    }

    /**
     * https://github.com/encero/sjtsk2gps
     */
    fun encero(jtsk: Jtsk): Wgs84 {
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
        val e2 = 1f - (1 - 1f / f1) * (1 - 1f / f1)
        val ro2 = a2 / sqrt(1 - e2 * sin(Bjtsk) * sin(Bjtsk))
        val x2 = (ro2 + jtsk.altitude) * cos(Bjtsk) * cos(Ljtsk)
        val y2 = (ro2 + jtsk.altitude) * cos(Bjtsk) * sin(Ljtsk)
        val z2 = ((1f - e2) * ro2 + jtsk.altitude) * sin(Bjtsk)

        val dx = 570.69
        val dy = 85.69
        val dz = 462.84
        val wz = -5.2611 / 3600 * PI / 180
        val wy = -1.58676 / 3600 * PI / 180
        val wx = -4.99821 / 3600 * PI / 180
        val m = 3.543e-6
        val xn = dx + (1f + m) * (x2 + wz * y2 - wy * z2)
        val yn = dy + (1f + m) * (-wz * x2 + y2 + wx * z2)
        val zn = dz + (1f + m) * (wy * x2 - wx * y2 + z2)

        val a3 = 6378137.0
        val f2 = 298.257223563
        val aB = f2 / (f2 - 1)
        val p = sqrt(xn * xn + yn * yn)
        val e22 = 1f - (1 - 1f / f2) * (1 - 1f / f2)
        val theta = atan(zn * aB / p)
        val st = sin(theta)
        val ct = cos(theta)
        t = (zn + e22 * aB * a3 * st * st * st) / (p - e22 * a3 * ct * ct * ct)
        val B = atan(t)
        val L = 2 * atan(yn / (p + xn))
        val hOut = sqrt(1 + t * t) * (p - a3 / sqrt(1f + (1f - e22) * t * t))

        val lat = B * 180 / PI
        val long = L * 180 / PI
        val height = floor(hOut * 100) / 100

        return Wgs84(lat, long, height, jtsk)
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
    /**
     * Czech around 50
     */
    val latitude: Double,
    /**
     * Czech around 14
     */
    val longitude: Double,
    val altitude: Double?,
    val jtsk: Jtsk,
)

@Serializable
data class Point(
    /**
     * Czech around 50
     */
    val latitude: Double,
    /**
     * Czech around 14
     */
    val longitude: Double,
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
    /**
     * Longitude, Latitude, Altitude / Elevation
     */
    val coordinates: List<Double>,
)

@Serializable
data class Properties(
    @SerialName("Altitude")
    val altitude: String,
    @SerialName("ETRS89 X")
    val jtskX: String,
    @SerialName("ETRS89 Y")
    val jtskY: String,
    @SerialName("ETRS89 Altitude")
    val jtskAltitude: String,
    @SerialName("GPS X")
    val latitude: String,
    @SerialName("GPS Y")
    val longitude: String,
)

fun List<Wgs84>.toGeoJson(): GeoJson {
    return GeoJson(
        features = this.map {
            Feature(
                geometry = Geometry(
                    coordinates = listOfNotNull(it.longitude, it.latitude, it.altitude),
                ),
                properties = Properties(
                    altitude = it.altitude.toString().replace(".", ","),
                    longitude = it.longitude.toString().replace(".", ","),
                    latitude = it.latitude.toString().replace(".", ","),
                    jtskX = it.jtsk.coordinateX.toString().replace(".", ","),
                    jtskY = it.jtsk.coordinateY.toString().replace(".", ","),
                    jtskAltitude = it.jtsk.altitude.toString().replace(".", ","),

                ),
            )
        }
    )
}