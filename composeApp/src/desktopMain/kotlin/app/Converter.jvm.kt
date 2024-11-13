package app

import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate

class ConverterJvm(
    source: String,
    target: String,
) : Converter {
    private val crsFactory = CRSFactory()
    private val sourceCrs = crsFactory.createFromName(source)
    private val targetCrs = crsFactory.createFromName(target)

    private val ctFactory = CoordinateTransformFactory()
    private val transformer = ctFactory.createTransform(sourceCrs, targetCrs)

    override fun convert(jtsk: Jtsk): Wgs84 {
        // `result` is an output parameter to `transform()`
        val result = ProjCoordinate()
        transformer.transform(ProjCoordinate(jtsk.coordinateX, jtsk.coordinateY, jtsk.altitude), result)
        return Wgs84(result.y, result.x, result.z.takeIf { !it.isNaN() }, jtsk)
    }
}

actual fun getConverter(source: String, target: String): Converter = ConverterJvm(source, target)