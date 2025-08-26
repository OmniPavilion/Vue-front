package common.utils

import common.exception.FileException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.jsoup.Jsoup
import org.springframework.ai.document.Document
import org.springframework.ai.reader.ExtractedTextFormatter
import org.springframework.ai.reader.pdf.PagePdfDocumentReader
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

/**
 * 分割Resource为List<Document>的供给类
 */
@Component
object ResourceUtils {

    /**
     * 根据文件类型将Resource转换为Document列表
     * @param file 文件资源
     * @return 文档列表
     * @throws IOException 文件读取异常
     * @throws IllegalArgumentException 不支持的文件类型
     */
    fun parseResourceToDocuments(file: File): List<Document> {
        val extension = file.name.substringAfterLast(".")

        val resource = FileSystemResource(file)

        return when (extension) {
            "pdf" -> parsePdf(resource)
            "doc" -> parseDoc(resource)
            "docx" -> parseDocx(resource)
            "txt" -> parseTxt(resource)
            "xlsx", "xls" -> parseXlsx(resource)
            "csv" -> parseCsv(resource)
            "md", "markdown" -> parseMarkdown(resource)
            else -> throw FileException("不支持的文件类型: $extension")
        }
    }

    /**
     * 解析PDF文件
     */
    private fun parsePdf(resource: Resource): List<Document> {
        val reader = PagePdfDocumentReader(
            resource,
            PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults()) // 默认参数
                .withPagesPerDocument(1) // 每1页PDF作为一个Document
                .build()
        )


        val documents: List<Document> = reader.read()
        if (documents.isEmpty()) {
            throw FileException("文件为空")
        }
        return documents
    }

    /**
     * 解析DOCX文档
     */
    private fun parseDocx(resource: Resource): List<Document> {
        return resource.inputStream.use { inputStream ->
            XWPFDocument(inputStream).use { doc ->
                val pages = mutableListOf<StringBuilder>().apply { add(StringBuilder()) }
                var currentLineCount = 0
                val maxLinesPerPage = 20 // 模拟每页行数（需根据实际文档调整）

                doc.paragraphs.forEach { paragraph ->
                    val lines = paragraph.text.split("\n")

                    lines.forEach { line ->
                        if (currentLineCount >= maxLinesPerPage) {
                            pages.add(StringBuilder(line))
                            currentLineCount = 0
                        } else {
                            pages.last().append(line).append("\n")
                        }
                        currentLineCount++
                    }
                }

                pages.mapIndexed { pageNum, content ->
                    Document(
                        content.toString(),
                        mapOf(
                            "doc_type" to "docx",
                            "page_number" to (pageNum + 1)
                        )
                    )
                }
            }
        }
    }

    /**
     * 解析DOC文档
     */
    private fun parseDoc(resource: Resource): List<Document> {
        return resource.inputStream.use { inputStream ->
            HWPFDocument(inputStream).use { doc ->
                val pages = mutableListOf<StringBuilder>().apply { add(StringBuilder()) }
                var currentLineCount = 0
                val maxLinesPerPage = 20 // 模拟每页行数

                WordExtractor(doc).paragraphText.forEach { paragraph ->
                    paragraph.split("\n").forEach { line ->
                        if (currentLineCount >= maxLinesPerPage) {
                            pages.add(StringBuilder(line))
                            currentLineCount = 0
                        } else {
                            pages.last().append(line).append("\n")
                        }
                        currentLineCount++
                    }
                }

                pages.mapIndexed { pageNum, content ->
                    Document(
                        content.toString(),
                        mapOf(
                            "doc_type" to "doc",
                            "page_number" to (pageNum + 1)
                        )
                    )
                }
            }
        }
    }

    /**
     * 解析纯文本文件
     */
    private fun parseTxt(resource: Resource): List<Document> {
        return resource.inputStream.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.lineSequence()
                    .filter { it.isNotBlank() }
                    .mapIndexed { index, line ->
                        Document(
                            line,
                            mutableMapOf<String, Any>().apply { // 使用明确类型的Map
                                put("source", resource.filename ?: "unknown")
                                put("line_number", index + 1)
                            }
                        )
                    }
                    .toList()
            }
        }
    }

    /**
     * 解析Excel文件（支持按Sheet或按行分割）
     * @param splitBySheet 是否按工作表分割（默认false），false时按行分割
     * @param withHeader 是否将首行作为列头（按行分割时生效）
     */
    private fun parseXlsx(
        resource: Resource,
        splitBySheet: Boolean = false,
        withHeader: Boolean = true
    ): List<Document> {
        return resource.inputStream.use { inputStream ->
            WorkbookFactory.create(inputStream).use { workbook ->
                if (workbook is XSSFWorkbook) {
                    if (splitBySheet) {
                        parseBySheets(workbook, resource.filename)
                    } else {
                        parseByRows(workbook, resource.filename, withHeader)
                    }
                } else {
                    throw IllegalArgumentException("Only .xlsx format is supported")
                }
            }
        }
    }

    /**
     * 按工作表分割
     */
    private fun parseBySheets(workbook: XSSFWorkbook, filename: String?): List<Document> {
        return workbook.sheetIterator().asSequence().map { sheet ->
            val content = StringBuilder().apply {
                appendLine("Sheet: ${sheet.sheetName}")
                sheet.rowIterator().forEach { row ->
                    row.cellIterator().forEach { cell ->
                        append(cell.toString()).append("\t")
                    }
                    appendLine()
                }
            }.toString()

            Document(
                content,
                mutableMapOf<String, Any>().apply {
                    put("source", filename ?: "unknown")
                    put("sheet_name", sheet.sheetName)
                    put("content_type", "excel_sheet")
                    put("row_count", sheet.physicalNumberOfRows)
                }
            )
        }.toList()
    }

    /**
     * 按行分割
     * @param chunkSize 指定每多少行分割为一个文档，为 null 时不分块
     */
    private fun parseByRows(
        workbook: XSSFWorkbook,
        filename: String?,
        withHeader: Boolean,
        chunkSize: Int? = 10
    ): List<Document> {
        return workbook.sheetIterator().asSequence().flatMap { sheet ->
            val headers = if (withHeader && sheet.physicalNumberOfRows > 0) {
                sheet.getRow(0)?.map { it.toString() } ?: emptyList()
            } else {
                emptyList()
            }

            val rows = sheet.rowIterator().asSequence()
                .let { seq -> if (withHeader) seq.drop(1) else seq } // 跳过标题行

            if (chunkSize == null) {
                // 不分块的情况，保持原有逻辑
                rows.mapIndexed { rowIndex, row ->
                    createDocument(row, headers, filename, sheet.sheetName, rowIndex, withHeader)
                }
            } else {
                // 分块处理
                rows.chunked(chunkSize).mapIndexed { chunkIndex, chunkRows ->
                    val content = if (headers.isNotEmpty()) {
                        // 对于有表头的情况，合并多行为一个文档
                        chunkRows.flatMapIndexed { relativeRowIndex, row ->
                            headers.mapIndexed { colIndex, header ->
                                "$header: ${row.getCell(colIndex)?.toString() ?: ""}"
                            }
                        }.joinToString("\n")
                    } else {
                        // 无表头情况，用制表符分隔单元格，换行符分隔行
                        chunkRows.joinToString("\n") { row ->
                            row.cellIterator().asSequence().joinToString("\t") { it.toString() }
                        }
                    }

                    Document(
                        content,
                        mutableMapOf<String, Any>().apply {
                            put("source", filename ?: "unknown")
                            put("sheet_name", sheet.sheetName)
                            put("chunk_index", chunkIndex)
                            put("row_range", "${chunkIndex * chunkSize + if (withHeader) 2 else 1}..${chunkIndex * chunkSize + if (withHeader) 1 else 0 + chunkSize}")
                            put("content_type", "excel_row_chunk")
                        }
                    )
                }
            }
        }.toList()
    }

    private fun createDocument(
        row: Row,
        headers: List<String>,
        filename: String?,
        sheetName: String,
        rowIndex: Int,
        withHeader: Boolean
    ): Document {
        val content = if (headers.isNotEmpty()) {
            headers.mapIndexed { colIndex, header ->
                "$header: ${row.getCell(colIndex)?.toString() ?: ""}"
            }.joinToString("\n")
        } else {
            row.cellIterator().asSequence().joinToString("\t") { it.toString() }
        }

        return Document(
            content,
            mutableMapOf<String, Any>().apply {
                put("source", filename ?: "unknown")
                put("sheet_name", sheetName)
                put("row_number", rowIndex + if (withHeader) 2 else 1)
                put("content_type", "excel_row")
            }
        )
    }

    /**
     * 解析CSV文件（支持带标题行）
     * @param hasHeader 是否首行为标题行（默认true）
     * @param delimiter 分隔符（默认,）
     */
    private fun parseCsv(
        resource: Resource,
        hasHeader: Boolean = true,
        delimiter: Char = ','
    ): List<Document> {
        return resource.inputStream.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                val csvFormat = CSVFormat.Builder.create()
                    .setDelimiter(delimiter)
                    .setHeader().setSkipHeaderRecord(hasHeader)
                    .build()

                CSVParser(reader, csvFormat).use { parser ->
                    parser.records.map { record ->
                        Document(
                            // 将行数据转为JSON格式
                            record.toMap().entries.joinToString("\n") { (k, v) -> "$k: $v" },
                            mutableMapOf<String, Any>().apply {
                                put("source", resource.filename ?: "unknown")
                                put("line_number", record.recordNumber)
                                put("content_type", "csv")
                                if (hasHeader) put("headers", parser.headerNames)
                            }
                        )
                    }
                }
            }
        }
    }

    /**
     * 解析Markdown文件（严格按标题分割）
     */
    private fun parseMarkdown(resource: Resource): List<Document> {
        return resource.inputStream.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                val text = reader.readText()
                val documents = mutableListOf<Document>()
                val sections = mutableListOf<Pair<String, StringBuilder>>().apply {
                    add("Untitled" to StringBuilder()) // 默认节
                }

                text.lineSequence().forEach { line ->
                    when {
                        // 检测二级标题
                        line.startsWith("## ") -> {
                            sections.add(line.trim() to StringBuilder())
                        }
                        // 其他内容归入当前节
                        line.isNotBlank() -> {
                            sections.last().second.append(line).append("\n")
                        }
                    }
                }

                sections.filter { it.second.isNotBlank() }.map { (title, content) ->
                    Document(
                        content.toString().trim(),
                        mapOf(
                            "source" to (resource.filename ?: "unknown"),
                            "title" to title,
                            "title_level" to if (title == "Untitled") 0 else 2,
                            "content_type" to "markdown"
                        )
                    )
                }
            }
        }
    }
}