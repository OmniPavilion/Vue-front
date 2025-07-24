package application

import common.constant.RedisConstant
import jakarta.annotation.Resource
import mu.KotlinLogging
import music.constant.MusicRedisConstant
import music.service.CategoryService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate

@SpringBootTest
class ApplicationTests{

    private var stringRedisTemplate: StringRedisTemplate = StringRedisTemplate()


    @Autowired
    private lateinit var categoryService: CategoryService

    private val logger = KotlinLogging.logger {}


    @Test
    fun contextLoads() {
        val tempPath = stringRedisTemplate.opsForHash<String, String>()
            .get(RedisConstant.GENERAL_KEY, RedisConstant.TEMP_FIELD)
            ?.takeIf { it.isNotBlank() }  // 检查Redis配置是否为空
            ?: System.getProperty("java.io.tmpdir")  // 回退到系统临时目录
        logger.info { "rootPath: $tempPath" }
    }
}