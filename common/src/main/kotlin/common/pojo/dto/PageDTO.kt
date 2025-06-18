package common.pojo.dto

import common.enumerate.SortDirection

class PageDTO<T> {
    var pageNum: Int = 0 // 页码
    var pageSize: Int = 0
    var order: SortDirection = SortDirection.ASC // 排序
    var query: T? = null
}