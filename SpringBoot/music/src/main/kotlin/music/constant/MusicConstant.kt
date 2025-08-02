package music.constant

import common.exception.FileException
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class MusicConstant(
      private val stringRedisTemplate: StringRedisTemplate
) {
     lateinit var rootPath : String
     lateinit var musicRootPath: String
     lateinit var singerRootPath: String
     lateinit var defaultSingerPath: String

     lateinit var DEFAULTPATH: String

     lateinit var DEFAULT: String

     //构造函数
     init {
          init()
     }

     fun init() {
          val opsForHash = stringRedisTemplate.opsForHash<String, String>()

          rootPath = opsForHash
               .get(MusicRedisConstant.FILE_KEY, MusicRedisConstant.ROOT_FIELD)
               ?: throw FileException("请先设置音乐根目录")

          val musicPath = opsForHash
               .get(MusicRedisConstant.FILE_KEY, MusicRedisConstant.MUSIC_ROOT_FIELD)
               ?: throw FileException("请先设置音乐根目录")
          val  singerPath = opsForHash
               .get(MusicRedisConstant.FILE_KEY, MusicRedisConstant.SINGER_ROOT_FIELD)
               ?: throw FileException("请先设置歌手根目录")

          DEFAULTPATH = opsForHash
               .get(MusicRedisConstant.FILE_KEY, MusicRedisConstant.DEFAULT_SINGER_FIELD)
               ?: throw FileException("请先设置默认歌手目录")

          musicRootPath = rootPath + musicPath
          singerRootPath = rootPath + singerPath
          defaultSingerPath = singerRootPath + DEFAULTPATH
          DEFAULT = DEFAULTPATH.substring(0, DEFAULTPATH.length - 1)
     }
}