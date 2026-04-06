import kr.dogfoot.hwplib.`object`.HWPFile
import kr.dogfoot.hwplib.reader.HWPReader
import kr.dogfoot.hwplib.tool.textextractor.TextExtractMethod
import kr.dogfoot.hwplib.tool.textextractor.TextExtractor

fun readHwp(filepath: String): HWPFile? {
    return try {
        val hwpFile: HWPFile = HWPReader.fromFile(filepath)
        if (hwpFile.bodyText.sectionList.size > 0) {
            println("$filepath read success!")
            hwpFile
        } else {
            println("$filepath read fail - no sections!")
            null
        }
    } catch (e: Exception) {
        System.err.println("$filepath read error: ${e.message}")
        throw e  // Main.kt에서 catch하여 폴백 처리
    }
}

fun extractTextHwp(hwpFile: HWPFile, textExtractMethod: TextExtractMethod): String {
    return TextExtractor.extract(hwpFile, textExtractMethod)
}
