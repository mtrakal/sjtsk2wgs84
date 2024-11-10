package cz.mtrakal.sjtsk2wgs84

import com.fleeksoft.ksoup.Ksoup

class GmlReaderUseCase {
    suspend operator fun invoke(filePath: String): List<Jtsk> {
        val document = Ksoup.parseFile(filePath = filePath)
        val select = document.getElementsByTag("gml:pos")

        val jtskList = mutableListOf<Jtsk>()
        select.listIterator().forEach { gmlPos ->
            val (x: Double, y: Double, h: Double) = gmlPos.text().split(' ').map {
                it.toDouble()
            }
            jtskList.add(Jtsk(x, y, h))
        }
        return jtskList
    }
}