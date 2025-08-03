package common.pojo.dto

import common.enumerate.SortDirection

class PageDTO<T> {
    val pageNum: Int = 0 // 页码
    val pageSize: Int = 0
    val order: SortDirection = SortDirection.ASC // 排序
    val query: T? = null
}