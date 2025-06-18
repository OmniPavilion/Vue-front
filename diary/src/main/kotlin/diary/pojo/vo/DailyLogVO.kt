package diary.pojo.vo

import java.time.LocalDate

// 日期分组的日志视图对象
data class DailyLogVO(
    var id  : Int,
    var date: LocalDate, // 日期
    var weather: String, // 天气情况
    var logs: MutableList<LogEntryVO>, // 该日期下的所有日志条目
)