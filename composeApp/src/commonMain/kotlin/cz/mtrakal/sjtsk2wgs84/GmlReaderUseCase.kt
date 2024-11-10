package cz.mtrakal.sjtsk2wgs84

import com.fleeksoft.ksoup.Ksoup

class GmlReaderUseCase {
    suspend operator fun invoke(filePath: String) {
        val document = Ksoup.parseFile(filePath = filePath)
        document.select("gml:controlPoint").listIterator().forEach { gmlPos ->
            val (x: Double, y: Double, h: Double) = gmlPos.data().split(' ').map {
                it.toDouble()
            }
        }
    }
}