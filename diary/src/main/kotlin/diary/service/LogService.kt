// LogService.kt
package diary.service

import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import diary.pojo.dto.LogQuery
import diary.pojo.vo.DailyLogVO

interface LogService {
    // 创建日志 (返回创建的日志ID)
    fun createLog(dailyLogVO: DailyLogVO): Int

    // 根据ID删除日志
    fun deleteLogById(id: Int)

    // 更新日志
    fun updateLog(dailyLogVO: DailyLogVO)

    // 根据ID查询日志
    fun getLogById(id: Int): DailyLogVO

    // 分页查询日志
    fun getLogsByPage(pageDTO: PageDTO<LogQuery>): PageVO<DailyLogVO>
}