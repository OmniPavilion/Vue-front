package bibliophile.constant

import common.exception.FileException
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class AiReaderFileConstant(
      private val stringRedisTemplate: StringRedisTemplate
) {
     lateinit var rootPath : String

     lateinit var defaultRootPath: String

     //构造函数
     init {
          init()
     }

     fun init() {
          val opsForHash = stringRedisTemplate.opsForHash<String, String>()

          rootPath = opsForHash.get(AiReaderFileRedisConstant.FILE_KEY, AiReaderFileRedisConstant.ROOT_FIELD)
               ?: throw FileException("请先设置文章根目录")

          defaultRootPath = opsForHash.get(AiReaderFileRedisConstant.FILE_KEY, AiReaderFileRedisConstant.DEFAULT_ROOT_FIELD)
               ?: throw FileException("请先设置文章默认根目录")
     }
}