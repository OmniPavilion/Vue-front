package common.pojo.vo

data class PageVO<T>(
    var total: Long = 0,
    var rows: List<T>? = null
)