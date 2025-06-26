package common.utils

import common.exception.FileException
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.util.FileCopyUtils
import java.io.*
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * 基于Spring Resource的文件工具类
 * 支持处理：classpath、文件系统、URL等资源
 */
object ResourceFileUtils {

    private val resolver = PathMatchingResourcePatternResolver()

    /**
     * 获取资源对象
     * @param location 资源路径（支持classpath:、file:、http:等前缀）
     */
    @Throws(FileException::class)
    fun getResource(location: String): Resource {
        return try {
            resolver.getResource(location)
        } catch (e: Exception) {
            throw FileException("获取资源失败: $location").apply {
                addSuppressed(e)
            }
        }
    }

    /**
     * 读取资源内容为字符串
     */
    @Throws(FileException::class)
    fun readResourceToString(location: String, charset: String = StandardCharsets.UTF_8.name()): String {
        return getResource(location).inputStream.use { input ->
            try {
                InputStreamReader(input, charset).use { reader ->
                    reader.readText()
                }
            } catch (e: Exception) {
                throw FileException("读取资源内容失败: $location").apply {
                    addSuppressed(e)
                }
            }
        }
    }

    /**
     * 复制资源到目标位置
     * @param sourceLocation 源资源路径
     * @param targetFile 目标文件（必须是文件系统路径）
     */
    @Throws(FileException::class)
    fun copyResourceToFile(sourceLocation: String, targetFile: File) {
        try {
            val source = getResource(sourceLocation)
            if (!targetFile.parentFile.exists()) {
                targetFile.parentFile.mkdirs()
            }
            source.inputStream.use { input ->
                FileOutputStream(targetFile).use { output ->
                    FileCopyUtils.copy(input, output)
                }
            }
        } catch (e: Exception) {
            throw FileException("复制资源到文件失败: $sourceLocation -> ${targetFile.path}").apply {
                addSuppressed(e)
            }
        }
    }

    /**
     * 将资源转换为可访问的URL
     */
    @Throws(FileException::class)
    fun getResourceUrl(location: String): URL {
        return try {
            getResource(location).url
        } catch (e: Exception) {
            throw FileException("获取资源URL失败: $location").apply {
                addSuppressed(e)
            }
        }
    }

    /**
     * 检查资源是否存在
     */
    fun exists(location: String): Boolean {
        return try {
            getResource(location).exists()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 获取资源最后修改时间
     */
    @Throws(FileException::class)
    fun lastModified(location: String): Long {
        return try {
            getResource(location).lastModified()
        } catch (e: Exception) {
            throw FileException("获取资源修改时间失败: $location").apply {
                addSuppressed(e)
            }
        }
    }

    /**
     * 将文件系统资源转换为Spring Resource
     */
    fun File.toResource(): Resource {
        return UrlResource(this.toURI())
    }

    /**
     * 安全地将Resource转为File（仅支持文件系统资源）
     */
    @Throws(FileException::class)
    fun Resource.toFileOrThrow(): File {
        return try {
            this.file
        } catch (e: Exception) {
            throw FileException("资源无法转换为文件: ${this.description}").apply {
                addSuppressed(e)
            }
        }
    }

    /**
     * 查找匹配模式的多个资源
     */
    @Throws(FileException::class)
    fun findResources(pattern: String): Array<Resource> {
        return try {
            resolver.getResources(pattern)
        } catch (e: Exception) {
            throw FileException("查找资源失败: $pattern").apply {
                addSuppressed(e)
            }
        }
    }
}