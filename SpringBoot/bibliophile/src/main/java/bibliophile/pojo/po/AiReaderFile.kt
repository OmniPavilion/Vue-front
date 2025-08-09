import com.baomidou.mybatisplus.annotation.*
import java.time.LocalDateTime

@TableName
data class AiReaderFile(
    @TableId(type = IdType.AUTO)
    val id: Long? = null,

    val title: String,

    val fileName: String,

    val fileSize: Long,

    var aiProcessed: Int = 0,

    val createdAt: LocalDateTime? = null,

    var updatedAt: LocalDateTime? = null,


) {
    @TableField(exist = false)  // 表示该字段不存在于数据库中
    var url: String? = null
}