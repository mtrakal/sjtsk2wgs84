package app

import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateReferenceSystem
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate

class ConverterJvm(source: String, target: String) : Converter {
    val crsFactory = CRSFactory()
    private val sourceCrs: CoordinateReferenceSystem = crsFactory.createFromName(source)
    private val targetCrs: CoordinateReferenceSystem = crsFactory.createFromName(target)

    //    constructor(
//        source: String,
//        target: String,
//        sourceParams: String,
//        targetParams: String,
//    ) {
//        val crsFactory = CRSFactory()
//        sourceCrs = crsFactory.createFromParameters(source, sourceParams)
//        targetCrs = crsFactory.createFromParameters(target, targetParams)
//    }

    private val ctFactory = CoordinateTransformFactory()
    private val transformer = ctFactory.createTransform(sourceCrs, targetCrs)

    override fun convert(jtsk: Jtsk): Wgs84 {
        // `result` is an output parameter to `transform()`
        val result = ProjCoordinate()
        transformer.transform(ProjCoordinate(jtsk.coordinateX, jtsk.coordinateY, jtsk.altitude), result)
        return Wgs84(result.y, result.x, result.z.takeIf { !it.isNaN() }, jtsk)
    }
}

actual fun getConverter(source: String, target: String): Converter = ConverterJvm(
    source, target
//    source = "UTM",
//    sourceParams = "+proj=utm +zone=33 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs",
//    target = "WGS84",
//    targetParams = "+proj=longlat +datum=WGS84 +no_defs"
)