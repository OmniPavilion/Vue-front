package common.enumerate

enum class SortDirection(val value: Int) {
    ASC(1),   // 升序
    DESC(-1),  // 降序
    RANDOM(0) // 随机
}