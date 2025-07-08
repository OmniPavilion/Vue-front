package music.controller

import common.exception.FileException
import common.pojo.vo.Result
import mu.KotlinLogging
import music.constant.MusicRedisConstant
import music.exception.MusicException
import music.service.MusicFileService
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.ContentDisposition
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.FileInputStream
import kotlin.math.log

@RestController
@RequestMapping("/api/musicFile")
class MusicFileController(
    private val musicFileService: MusicFileService,
    private val stringRedisTemplate: StringRedisTemplate
) {
    private val logger = KotlinLogging.logger {}

    // 修改根文件夹
    @PutMapping("/root")
    fun updateRoot(@RequestParam path: String) : Result<Unit> {
        logger.info { "将音乐文件根文件夹修改为： $path" }
        musicFileService.updateRoot(path)
        return Result.success()
    }

    // 重置音乐文件根文件夹
    @PutMapping("/resetRoot")
    fun resetRoot() : Result<Unit> {
        logger.info { "将音乐文件根文件夹重置为默认值" }
        val defaultPath = stringRedisTemplate.opsForHash<String, String>()
            .get(MusicRedisConstant.FILE_KEY, MusicRedisConstant.DEFAULT_ROOT_FIELD)
            ?: throw FileException("请先设置默认音乐根目录")
        musicFileService.updateRoot(defaultPath)
        return Result.success()
    }

    //获取音乐根目录
    @GetMapping("/root")
    fun getRoot(): Result<String> {
        val root = stringRedisTemplate.opsForHash<String, String>().get(MusicRedisConstant.FILE_KEY, MusicRedisConstant.ROOT_FIELD)
        if (root != null) {
            Result.success(root)
        } else {
            throw MusicException("未设置音乐根目录")
        }
        return Result.success(root)
    }
}