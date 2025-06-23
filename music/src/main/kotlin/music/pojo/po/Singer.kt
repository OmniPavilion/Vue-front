package music.pojo.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName
data class Singer(
    @TableId
    val singerId: Long = 0,
    val name: String,
    val coverPath: String? = null,
    val createTime: LocalDateTime = LocalDateTime.now(),
    val updateTime: LocalDateTime = LocalDateTime.now()
)