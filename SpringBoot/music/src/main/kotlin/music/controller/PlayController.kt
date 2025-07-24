package music.controller

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.fasterxml.jackson.databind.ser.Serializers
import common.pojo.vo.Result
import mu.KotlinLogging
import music.constant.MusicRedisConstant
import music.constant.PlayModeConstant
import music.pojo.dto.PlayArg
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/play")
class PlayController(
    private val stringRedisTemplate: StringRedisTemplate
) {
    private val logger = KotlinLogging.logger {}

    // 获取播放参数
    @GetMapping("/arg")
    fun getPlayArg(): Result<PlayArg> {

        logger.info { "获取播放参数" }

        val opsForHash = stringRedisTemplate.opsForHash<String, String>()

        val currentMusicId = opsForHash
            .get(MusicRedisConstant.PLAY_KEY, MusicRedisConstant.CURRENT_MUSIC_ID_FIELD)?.toInt()
        val playMode = opsForHash
            .get(MusicRedisConstant.PLAY_KEY, MusicRedisConstant.PLAY_MODE_FIELD)
        val volume = opsForHash
            .get(MusicRedisConstant.PLAY_KEY, MusicRedisConstant.VOLUME_FIELD)?.toFloat()
        val playDuration = opsForHash
            .get(MusicRedisConstant.PLAY_KEY, MusicRedisConstant.PLAY_DURATION_FIELD)?.toInt()

        val playArg = PlayArg(
            currentMusicId = currentMusicId,
            playMode = playMode ?: PlayModeConstant.LOOP,
            volume = volume ?: 0.5f,
            playDuration = playDuration ?: 0
        )
        return Result.success(playArg)
    }

    // 更新播放参数
    @PostMapping("/arg")
    fun updatePlayArg(@RequestBody playArg: PlayArg): Result<Unit> {

        logger.info { "更新播放参数: ${JSON.toJSONString(playArg, SerializerFeature.PrettyFormat)}" }
        val opsForHash = stringRedisTemplate.opsForHash<String, String>()

        opsForHash.put(
            MusicRedisConstant.PLAY_KEY,
            MusicRedisConstant.CURRENT_MUSIC_ID_FIELD,
            playArg.currentMusicId.toString()
        )
        opsForHash.put(
            MusicRedisConstant.PLAY_KEY,
            MusicRedisConstant.PLAY_MODE_FIELD,
            playArg.playMode)
        opsForHash.put(
            MusicRedisConstant.PLAY_KEY,
            MusicRedisConstant.VOLUME_FIELD,
            playArg.volume.toString())
        opsForHash.put(
            MusicRedisConstant.PLAY_KEY,
            MusicRedisConstant.PLAY_DURATION_FIELD,
            playArg.playDuration.toString())
        return Result.success()
    }
}