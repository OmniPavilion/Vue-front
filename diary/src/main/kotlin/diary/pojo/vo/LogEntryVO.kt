package diary.pojo.vo

// 单条日志条目视图对象
data class LogEntryVO  // 构造函数、Getter和Setter方法
    (
    var activity: String, // 活动内容
    var category: String, // 分类名称
)