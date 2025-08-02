package common.utils

import common.exception.FileException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
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

        // 时间戳
        val timestamp = System.currentTimeMillis()
        val targetFile = File(TEMP_PATH, timestamp.toString() + safeFilename)
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
    fun addFileWithDeleteBefore(sourceFile: File, targetPath: String) {
        addFile(sourceFile, targetPath)
        deleteFile(sourceFile.toString())
    }
    /**
     * 添加/创建文件
     * @param sourceFile 源文件（用于复制内容）
     * @param targetPath 目标路径（相对路径或绝对路径）
     * @throws FileException 当操作失败时抛出
     */
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
        addFileWithDeleteBefore(source, targetPath)
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

    /**
     * 重命名文件夹
     * @param folderPath 文件夹路径（相对路径或绝对路径）
     * @param newName 新文件夹名称
     * @throws FileException 当操作失败时抛出
     */
    @Throws(FileException::class)
    fun renameFolder(folderPath: String, newName: String) {
        val folder = Paths.get(folderPath).toFile()

        // 验证原文件夹是否存在
        if (!folder.exists()) {
            throw FileException("文件夹不存在: $folderPath")
        }
        if (!folder.isDirectory) {
            throw FileException("路径不是文件夹: $folderPath")
        }

        // 验证新名称合法性
        if (newName.isBlank()) {
            throw FileException("新文件夹名称不能为空")
        }

        if (newName.contains(File.separator)) {
            throw FileException("新文件夹名称不能包含路径分隔符")
        }


        // 构建新路径
        val parentPath = folder.parent ?: throw FileException("无法获取父目录路径")
        val newFolder = File(parentPath, newName)

        // 检查目标文件夹是否已存在
        if (newFolder.exists()) {
            throw FileException("目标文件夹已存在: ${newFolder.absolutePath}")
        }

        // 执行重命名
        if (!folder.renameTo(newFolder)) {
            throw FileException("文件夹重命名失败: ${folder.absolutePath} -> ${newFolder.absolutePath}")
        }
    }

    /**
     * 移动文件夹
     * @param sourceFolderPath 源文件夹路径
     * @param targetFolderPath 目标文件夹路径
     * @throws FileException 当操作失败时抛出
     */
    @Throws(FileException::class)
    fun moveFolder(sourceFolderPath: String, targetFolderPath: String) {
        val sourceFolder = Paths.get(sourceFolderPath).toFile()
        val targetFolder = Paths.get(targetFolderPath).toFile()

        // 验证源文件夹是否存在
        if (!sourceFolder.exists()) {
            throw FileException("源文件夹不存在: $sourceFolderPath")
        }
        if (!sourceFolder.isDirectory) {
            throw FileException("源路径不是文件夹: $sourceFolderPath")
        }

        // 创建目标文件夹
        targetFolder.parentFile?.mkdirs()

        // 检查目标文件夹是否已存在
        if (targetFolder.exists()) {
            throw FileException("目标文件夹已存在: $targetFolderPath")
        }

        // 执行移动操作
        if (!sourceFolder.renameTo(targetFolder)) {
            throw FileException("文件夹移动失败: $sourceFolderPath -> $targetFolderPath")
        }
    }

    /**
     * 随机从文件夹中读取一个文件
     * @param folderPath 文件夹路径（相对路径或绝对路径）
     * @return 随机选中的文件对象
     * @throws FileException 当文件夹不存在、为空或读取失败时抛出
     */
    @Throws(FileException::class)
    fun getRandomFileFromFolder(folderPath: String): File {
        validateFolder(folderPath)

        val folder = Paths.get(folderPath).toFile()

        // 验证文件夹是否存在且可读
        if (!folder.exists()) {
            throw FileException("文件夹不存在: $folderPath")
        }
        if (!folder.isDirectory) {
            throw FileException("路径不是文件夹: $folderPath")
        }
        if (!folder.canRead()) {
            throw FileException("文件夹不可读: $folderPath")
        }

        // 获取文件夹内所有文件（排除子目录）
        val files = folder.listFiles { file -> file.isFile }
            ?.takeIf { it.isNotEmpty() }
            ?: throw FileException("文件夹为空: $folderPath")

        // 随机选择一个文件
        return files.random()
    }

    /**
     * 删除文件夹及其所有内容
     * @param folderPath 文件夹路径（相对路径或绝对路径）
     * @throws FileException 当操作失败时抛出
     */
    @Throws(FileException::class)
    fun deleteFolder(folderPath: String) {
        val folder = Paths.get(folderPath).toFile()

        // 验证文件夹是否存在
        if (!folder.exists()) {
            throw FileException("文件夹不存在: $folderPath")
        }
        if (!folder.isDirectory) {
            throw FileException("路径不是文件夹: $folderPath")
        }

        // 递归删除文件夹内容
        folder.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                deleteFolder(file.absolutePath) // 递归删除子文件夹
            } else {
                if (!file.delete()) {
                    throw FileException("文件删除失败: ${file.absolutePath}")
                }
            }
        }

        // 删除空文件夹
        if (!folder.delete()) {
            throw FileException("文件夹删除失败: $folderPath")
        }
    }

    /**
     * 安全删除文件夹（不抛出异常）
     * @param folderPath 文件夹路径
     * @return 是否删除成功
     */
    fun deleteFolderSafely(folderPath: String): Boolean {
        return try {
            deleteFolder(folderPath)
            true
        } catch (e: FileException) {
            false
        }
    }

    /**
     * 移动文件
     * @param sourceFilePath 源文件路径（相对路径或绝对路径）
     * @param targetFilePath 目标文件路径（相对路径或绝对路径）
     * @throws FileException 当操作失败时抛出
     */
    @Throws(FileException::class)
    fun moveFile(sourceFilePath: String, targetFilePath: String) {
        val sourceFile = Paths.get(sourceFilePath).toFile()
        val targetFile = Paths.get(targetFilePath).toFile()

        // 验证源文件是否存在
        if (!sourceFile.exists()) {
            throw FileException("源文件不存在: $sourceFilePath")
        }
        if (sourceFile.isDirectory) {
            throw FileException("源路径是文件夹而不是文件: $sourceFilePath")
        }

        // 创建目标文件的父目录
        targetFile.parentFile?.mkdirs()

        // 检查目标文件是否已存在
        if (targetFile.exists()) {
            throw FileException("目标文件已存在: $targetFilePath")
        }

        // 执行移动操作
        if (!sourceFile.renameTo(targetFile)) {
            // 如果重命名失败，尝试复制后删除
            try {
                Files.copy(
                    sourceFile.toPath(),
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
                if (!sourceFile.delete()) {
                    if (!targetFile.delete()) {
                        throw FileException("目标文件删除失败: $targetFilePath")
                    }
                    throw FileException("源文件删除失败: $sourceFilePath")
                }
            } catch (e: IOException) {
                throw FileException("文件移动失败: $sourceFilePath -> $targetFilePath ${e}")
            }
        }
    }

    /**
     * 将文件路径分割为路径组件集合（倒序返回）
     * @param path 文件路径（如 "src/images/1.png"）
     * @return 路径组件列表（倒序，如 ["1.png", "images", "src"]）
     */
    fun splitPathToComponents(path: String): List<String> {
        return path.split(File.separatorChar)
            .filter { it.isNotBlank() }
            .map { it.trim() }
            .reversed() // 添加这行实现倒序
    }

    /**
     * 遍历文件夹下的所有文件，返回文件绝对路径列表
     * @param folderPath 文件夹路径
     * @return 包含所有文件绝对路径的列表
     * @throws FileException 当文件夹不存在或不可读时抛出
     */
    @Throws(FileException::class)
    fun getFilePathsFromFolder(folderPath: String): List<String> {
        validateFolder(folderPath)
        return Paths.get(folderPath).toFile()
            .walk()
            .filter { it.isFile }
            .map { it.absolutePath }
            .toList()
    }

    /**
     * 遍历文件夹下的所有文件，返回分割后的路径组件列表（倒序）
     * @param folderPath 文件夹路径
     * @return 包含所有文件路径分割组件（倒序）的列表
     * @throws FileException 当文件夹不存在或不可读时抛出
     */
    @Throws(FileException::class)
    fun getFilePathsFromFolder(folderPath: String, isFiltered: Boolean): List<List<String>> {
        validateFolder(folderPath)
        return Paths.get(folderPath).toFile()
            .walk()
            .filter { it.isFile }
            .map { splitPathToComponents(it.absolutePath) }
            .toList()
    }

    /**
     * 验证文件夹路径是否有效
     * @param folderPath 文件夹路径
     * @throws FileException 当文件夹不存在、不是目录或不可读时抛出
     */
    @Throws(FileException::class)
    private fun validateFolder(folderPath: String) {
        val folder = Paths.get(folderPath).toFile()
        when {
            !folder.exists() -> throw FileException("文件夹不存在: $folderPath")
            !folder.isDirectory -> throw FileException("路径不是文件夹: $folderPath")
            !folder.canRead() -> throw FileException("文件夹不可读: $folderPath")
        }
    }

    /**
     * 检查文件或目录路径是否存在
     * @param path 要检查的路径（文件或目录）
     * @return Boolean - true表示存在，false表示不存在
     */
    fun pathExists(path: String): Boolean {
        return try {
            Paths.get(path).toFile().exists()
        } catch (e: Exception) {
            false // 如果路径非法也返回false
        }
    }
}