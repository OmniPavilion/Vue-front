package common.utils

import common.exception.FileException
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * 本地文件操作工具类
 * 仅支持处理本地文件系统的相对路径和绝对路径
 */
@Component
object MultipartFileUtils {

    private val logger = KotlinLogging.logger {}

    private const val TEMP_PATH = "./temp/"

    /**
     * 将 MultipartFile 转换为 File（仅支持文件系统资源）
     * @throws FileException 如果资源无法直接转换为 File
     */
    @Throws(FileException::class)
    fun getFileByMultipartFile(multipartFile: MultipartFile): File {
        val originalFilename = multipartFile.originalFilename
            ?: throw FileException("文件名不能为空")
        val safeFilename = originalFilename
            .replace("[^a-zA-Z0-9.-]".toRegex(), "_")  // 替换非法字符为下划线

        val targetFile = File(TEMP_PATH, safeFilename)
        targetFile.parentFile?.mkdirs()

        multipartFile.inputStream.use { input ->
            Files.copy(input, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        targetFile.deleteOnExit()

        return targetFile
    }

    /**
     * 添加/创建文件
     * @param sourceFile 源文件（用于复制内容）
     * @param targetPath 目标路径（相对路径或绝对路径）
     * @throws FileException 当操作失败时抛出
     */
    @Throws(FileException::class)
    fun addFile(sourceFile: File, targetPath: String) {
        val targetFile = Paths.get(targetPath).toFile()
        targetFile.parentFile?.mkdirs()

        if (!sourceFile.exists()) {
            throw FileException("源文件不存在: ${sourceFile.absolutePath}")
        }
        if (!sourceFile.canRead()) {
            throw FileException("源文件不可读: ${sourceFile.absolutePath}")
        }

        Files.copy(
            sourceFile.toPath(),
            targetFile.toPath(),
            StandardCopyOption.REPLACE_EXISTING
        )
    }

    /**
     * 获取文件对象
     * @param path 文件路径（相对路径或绝对路径）
     * @return 文件对象
     * @throws FileException 当文件不存在时抛出
     */
    @Throws(FileException::class)
    fun getFile(path: String): File {
        val file = Paths.get(path).toFile()
        if (!file.exists()) {
            throw FileException("文件不存在: $path")
        }
        return file
    }

    /**
     * 复制文件
     * @param sourcePath 源文件路径
     * @param targetPath 目标路径
     * @throws FileException 当操作失败时抛出
     */
    @Throws(FileException::class)
    fun copyFile(sourcePath: String, targetPath: String) {
        val source = getFile(sourcePath)
        addFile(source, targetPath)
    }

    /**
     * 删除文件
     * @param path 文件路径（相对路径或绝对路径）
     * @throws FileException 当操作失败时抛出
     */
    @Throws(FileException::class)
    fun deleteFile(path: String) {
        val file = Paths.get(path).toFile()
        if (!file.exists()) {
            throw FileException("文件不存在: $path")
        }
        if (!file.delete()) {
            throw FileException("文件删除失败: $path")
        }
    }

    /**
     * 读取文件内容为字符串
     * @param path 文件路径
     * @param charset 字符集（默认 UTF-8）
     * @return 文件内容
     * @throws FileException 当操作失败时抛出
     */
    @Throws(FileException::class)
    fun readFileToString(path: String, charset: String = StandardCharsets.UTF_8.name()): String {
        val file = getFile(path)
        return file.inputStream().use { input ->
            InputStreamReader(input, charset).use { reader ->
                reader.readText()
            }
        }
    }

    /**
     * 写入字符串内容到文件
     * @param path 文件路径
     * @param content 要写入的内容
     * @param charset 字符集（默认 UTF-8）
     * @throws FileException 当操作失败时抛出
     */
    @Throws(FileException::class)
    fun writeStringToFile(path: String, content: String, charset: String = StandardCharsets.UTF_8.name()) {
        val file = Paths.get(path).toFile()
        file.parentFile?.mkdirs()

        file.outputStream().use { output ->
            OutputStreamWriter(output, charset).use { writer ->
                writer.write(content)
            }
        }
    }
}