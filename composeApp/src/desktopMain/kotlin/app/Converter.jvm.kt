package app

import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate

class ConverterJvm : Converter {
    override fun convert(source: String, target: String, jtsk: Jtsk): Wgs84 {
        val crsFactory = CRSFactory()
        val sourceCrs = crsFactory.createFromName(source)
        val targetCrs = crsFactory.createFromName(target)

        val ctFactory = CoordinateTransformFactory()
        val transformer = ctFactory.createTransform(sourceCrs, targetCrs)

        // `result` is an output parameter to `transform()`
        val result = ProjCoordinate()
        transformer.transform(ProjCoordinate(jtsk.coordinateX, jtsk.coordinateY, jtsk.altitude), result)
        return Wgs84(result.y, result.x, result.z.takeIf { !it.isNaN() }, jtsk)
    }
}

actual fun getConverter(): Converter = ConverterJvm()