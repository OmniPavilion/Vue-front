package diary.pojo.dto

import diary.pojo.po.Category
import java.time.LocalDate
import java.util.Date

class LogQuery {
    // 分类
    var category: Category? = null
    // 开始日期
    var startDate: LocalDate? = null
    // 结束日期
    var endDate: LocalDate? = null
}