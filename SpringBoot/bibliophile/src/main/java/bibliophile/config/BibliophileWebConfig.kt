package bibliophile.config

import bibliophile.constant.AiReaderFileRedisConstant
import bibliophile.constant.FIlePathConstant.Companion.AI_READER_FILE_STATIC_PATH
import common.exception.FileException
import mu.KotlinLogging
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver


@Configuration
class BibliophileWebConfig(
    private val stringRedisTemplate: StringRedisTemplate
) : WebMvcConfigurer {

    private val logger = KotlinLogging.logger {}

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        try {
            val opsForHash = stringRedisTemplate.opsForHash<String, String>()

            // 1. 获取并验证路径
            val rootPath = opsForHash.get(AiReaderFileRedisConstant.FILE_KEY, AiReaderFileRedisConstant.ROOT_FIELD)
                ?.takeIf { it.isNotBlank() }
                ?: throw FileException("文件根目录未配置")



            // 2. 标准化路径
            val normalizedSingerPath = normalizePath(rootPath)
            logger.info("配置静态资源映射: $AI_READER_FILE_STATIC_PATH** → $normalizedSingerPath")

            // 3. 注册资源处理器
            registry.addResourceHandler("$AI_READER_FILE_STATIC_PATH**")
                .addResourceLocations(normalizedSingerPath)
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(PathResourceResolver())

        } catch (e: Exception) {
            logger.error("静态资源映射配置失败", e)
            throw e
        }
    }

    private fun normalizePath(rawPath: String): String {
        return rawPath
            .replace("\\", "/") // 统一分隔符
            .let { if (!it.endsWith("/")) "$it/" else it } // 确保结尾有/
            .let { if (!it.startsWith("file:")) "file:$it" else it } // 添加file:前缀
    }
}