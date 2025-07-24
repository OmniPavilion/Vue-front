package diary.pojo.po

import com.baomidou.mybatisplus.annotation.EnumValue

enum class Category(
    @EnumValue val id: Int? = null,
    val dbName: String,
) {
    STUDY(1, "Study"),
    WORK(2, "Work"),
    LIFE(3, "Life"),
    EXERCISE(4, "Exercise"),
    ENTERTAINMENT(5, "Entertainment"),
    SOCIAL(6, "Social");
}