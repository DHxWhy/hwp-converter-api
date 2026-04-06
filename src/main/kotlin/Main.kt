import io.javalin.Javalin
import kr.dogfoot.hwplib.tool.textextractor.TextExtractMethod
import java.io.File

fun main(args: Array<String>) {
    val app = Javalin.create().start(7000)

    app.post("/upload") { ctx ->
        println("connected")
        val file = ctx.uploadedFile("file") ?: throw IllegalArgumentException("No file uploaded")
        val savedFilepath = "sample_hwp/${file.filename()}"
        val savedFile = File(savedFilepath)
        file.content().copyTo(savedFile.outputStream())

        try {
            if (file.filename().endsWith(".hwp")) {
                val option = ctx.queryParam("option") ?: "main-only"
                val textExtractMethod = if (option == "all") {
                    TextExtractMethod.InsertControlTextBetweenParagraphText
                } else {
                    TextExtractMethod.OnlyMainParagraph
                }

                // 1차: 전체 파싱 시도
                var hwpText: String? = null
                try {
                    val hwpFile = readHwp(savedFile.path)
                    hwpText = hwpFile?.let { extractTextHwp(it, textExtractMethod) }
                } catch (e: Exception) {
                    System.err.println("[hwp] Full parse failed: ${e.message}, trying main-only fallback")
                    // 2차 폴백: main-only로 재시도 (테이블 제외 — 테이블에서 에러 발생 시 우회)
                    try {
                        val hwpFile = readHwp(savedFile.path)
                        hwpText = hwpFile?.let { extractTextHwp(it, TextExtractMethod.OnlyMainParagraph) }
                    } catch (e2: Exception) {
                        System.err.println("[hwp] Main-only fallback also failed: ${e2.message}")
                    }
                }

                removeFile(savedFilepath)

                if (hwpText != null && hwpText.isNotBlank()) {
                    ctx.contentType("text/plain")
                    ctx.result(hwpText)
                } else {
                    ctx.status(400)
                    ctx.result("extract file failed")
                }
            } else if (file.filename().endsWith(".hwpx")) {
                removeFile(savedFilepath)
                ctx.status(404)
                ctx.result("hwpx file does not support yet")
            } else {
                removeFile(savedFilepath)
                ctx.status(404)
                ctx.result("please upload hwp file")
            }
        } catch (e: Exception) {
            removeFile(savedFilepath)
            System.err.println("[hwp] Unexpected error: ${e.message}")
            ctx.status(500)
            ctx.result("server error: ${e.message}")
        }
    }

    // 헬스체크 엔드포인트
    app.get("/health") { ctx ->
        ctx.result("ok")
    }
}

private fun removeFile(filepath: String) {
    val file = File(filepath)
    if (file.exists()) {
        file.delete()
    }
}
