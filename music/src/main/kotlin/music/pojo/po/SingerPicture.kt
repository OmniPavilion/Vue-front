package music.pojo.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName
data class SingerPicture(
    @TableId
    var id: Long = 0,
    var singerId: Long,
    var fileName: String
)