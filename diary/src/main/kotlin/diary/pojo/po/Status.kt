package diary.pojo.po

import com.baomidou.mybatisplus.annotation.EnumValue

enum class Status(
    @EnumValue val id: Int? = null,
    val string: String
) {
    DRAFT(1, "draft"),
    PENDING(2, "pending"),
    IN_PROGRESS(3, "in-progress"),
    COMPLETED(4, "completed"),
    CANCELLED(5, "cancelled");
}