package music.config

import common.exception.FileException
import mu.KotlinLogging
import music.constant.FIlePathConstant.Companion.MUSIC_FILE_STATIC_PATH
import music.constant.FIlePathConstant.Companion.`SINGER_IMAGES_STATIC_PATH`
import music.constant.MusicRedisConstant
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver


@Configuration
class WebConfig(
    private val stringRedisTemplate: StringRedisTemplate
) : WebMvcConfigurer {

    private val logger = KotlinLogging.logger {}

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        try {
            val opsForHash = stringRedisTemplate.opsForHash<String, String>()

            // 1. 获取并验证路径
            val rootPath = opsForHash.get(MusicRedisConstant.FILE_KEY, MusicRedisConstant.ROOT_FIELD)
                ?.takeIf { it.isNotBlank() }
                ?: throw FileException("音乐根目录未配置")

            val singerPath = opsForHash.get(MusicRedisConstant.FILE_KEY, MusicRedisConstant.SINGER_ROOT_FIELD)
                ?.takeIf { it.isNotBlank() }
                ?: throw FileException("歌手目录未配置")

            val musicPath = opsForHash.get(MusicRedisConstant.FILE_KEY, MusicRedisConstant.MUSIC_ROOT_FIELD)
                ?.takeIf { it.isNotBlank() }
                ?: throw FileException("音乐目录未配置")

            // 2. 标准化路径
            val normalizedSingerPath = normalizePath(rootPath + singerPath)
            val normalizedMusicPath = normalizePath(rootPath + musicPath)
            logger.info("配置静态资源映射: $SINGER_IMAGES_STATIC_PATH** → $normalizedSingerPath")
            logger.info("配置静态资源映射: $MUSIC_FILE_STATIC_PATH** → $normalizedMusicPath")

            // 3. 注册资源处理器
            registry.addResourceHandler("$SINGER_IMAGES_STATIC_PATH**")
                .addResourceLocations(normalizedSingerPath)
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(PathResourceResolver())

            registry.addResourceHandler("$MUSIC_FILE_STATIC_PATH**")
                .addResourceLocations(normalizedMusicPath)
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