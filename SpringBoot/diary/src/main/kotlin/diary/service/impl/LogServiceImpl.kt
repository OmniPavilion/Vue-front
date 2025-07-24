package diary.service.impl

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.enumerate.SortDirection
import diary.exception.DailyException
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import diary.mapper.DailyActivityMapper
import diary.mapper.DailyTimeMapper
import diary.pojo.dto.LogQuery
import diary.pojo.po.Category
import diary.pojo.po.DailyActivity
import diary.pojo.po.DailyTime
import diary.pojo.po.Weather
import diary.pojo.vo.DailyLogVO
import diary.pojo.vo.LogEntryVO
import diary.service.LogService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@Datasource(DataSourceType.DIARY)
class LogServiceImpl(
    private val dailyTimeMapper: DailyTimeMapper,
    private val dailyActivityMapper: DailyActivityMapper
) : LogService {

    private val logger = KotlinLogging.logger {}
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日")

    @Transactional
    override fun createLog(dailyLogVO: DailyLogVO): Int {
        //  判断日期是否是未来
        if (dailyLogVO.date.isAfter(LocalDate.now())) {
            throw DailyException("日期不能是未来")
        }
        // 检查日志是否已存在
        val exists = dailyTimeMapper.selectCount(
            KtQueryWrapper(DailyTime::class.java)
                .eq(DailyTime::recordDate, dailyLogVO.date)
        ) > 0

        if (exists) {
            throw DailyException("${dailyLogVO.date.format(dateFormatter)}的日志已存在")
        }

        val dailyTime = DailyTime(
            id = 0, // 由数据库自动生成
            recordDate = dailyLogVO.date,
            weather = Weather.valueOf(dailyLogVO.weather.uppercase().replace(" ", "_")),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        dailyTimeMapper.insert(dailyTime)

        dailyLogVO.logs.forEach { logEntry ->
            val activity = logEntry?.let {
                DailyActivity(
                    id = 0, // 由数据库自动生成
                    dateId = dailyTime.id,
                    activity = it.activity,
                    category = Category.valueOf(logEntry.category.uppercase()),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            }
            dailyActivityMapper.insert(activity)
        }

        return dailyTime.id
    }

    @Transactional
    override fun deleteLogById(id: Int) {
        // 检查日志是否存在
        if (dailyTimeMapper.selectById(id) == null) {
            throw DailyException("日志不存在")
        }

        val queryWrapper = QueryWrapper<DailyActivity>().eq("date_id", id)
        dailyActivityMapper.delete(queryWrapper)
        dailyTimeMapper.deleteById(id)
    }

    @Transactional
    override fun updateLog(dailyLogVO: DailyLogVO) {
        // 1. 检查日志是否存在并获取原记录
        val existingDailyTime = dailyTimeMapper.selectById(dailyLogVO.id)
            ?: throw DailyException("日志不存在")

        // 2. 验证日期是否被修改
        if (dailyLogVO.date != existingDailyTime.recordDate) {
            throw DailyException("日志日期不可修改")
        }

        // 3. 更新天气信息
        val dailyTime = existingDailyTime.copy(
            weather = Weather.valueOf(dailyLogVO.weather.uppercase().replace(" ", "_")),
            updatedAt = LocalDateTime.now()
        )
        dailyTimeMapper.updateById(dailyTime)

        // 4. 更新活动记录
        val queryWrapper = KtQueryWrapper(DailyActivity::class.java).eq(DailyActivity::dateId, dailyLogVO.id)
        dailyActivityMapper.delete(queryWrapper)

        dailyLogVO.logs.forEach { logEntry ->
            val activity = logEntry?.let {
                DailyActivity(
                    id = 0, // 由数据库自动生成
                    dateId = dailyLogVO.id,
                    activity = it.activity,
                    category = Category.valueOf(logEntry.category.uppercase()),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            }
            dailyActivityMapper.insert(activity)
        }
    }

    override fun getLogById(id: Int): DailyLogVO {
        val dailyTime = dailyTimeMapper.selectById(id) ?: throw DailyException("日志不存在")

        val queryWrapper = QueryWrapper<DailyActivity>().eq("date_id", id)
        val activities = dailyActivityMapper.selectList(queryWrapper)

        val logEntries = activities.map {
            LogEntryVO(
                activity = it.activity,
                category = it.category.dbName
            )
        }

        return DailyLogVO(
            id = dailyTime.id,
            date = dailyTime.recordDate,
            weather = dailyTime.weather.dbName,
            logs = logEntries.toMutableList()
        )
    }

    override fun getLogsByPage(pageDTO: PageDTO<LogQuery>): PageVO<DailyLogVO> {
        val queryWrapper = KtQueryWrapper(DailyTime::class.java).apply {
            pageDTO.query?.let { query ->
                query.startDate?.let { ge(DailyTime::recordDate, it) }
                query.endDate?.let { le(DailyTime::recordDate, it.plusDays(1)) }

                when (pageDTO.order) {
                    SortDirection.ASC -> orderByAsc(DailyTime::recordDate)
                    SortDirection.DESC -> orderByDesc(DailyTime::recordDate)
                    SortDirection.RANDOM -> last("ORDER BY RAND()")
                }
            } ?: orderByDesc(DailyTime::recordDate)
        }

        val page = Page<DailyTime>(pageDTO.pageNum.toLong(), pageDTO.pageSize.toLong())
        logger.debug { "Query wrapper: ${JSON.toJSONString(queryWrapper, SerializerFeature.PrettyFormat)}" }

        val dailyTimePage = dailyTimeMapper.selectPage(page, queryWrapper)

        val dailyLogVOs = dailyTimePage.records.map { dailyTime ->
            val activityQueryWrapper = KtQueryWrapper(DailyActivity::class.java).apply {
                eq(DailyActivity::dateId, dailyTime.id)
                pageDTO.query?.category?.let { eq(DailyActivity::category, it) }
            }
            val activities = dailyActivityMapper.selectList(activityQueryWrapper)

            val logEntries = activities.map {
                LogEntryVO(
                    activity = it.activity,
                    category = it.category.dbName
                )
            }

            DailyLogVO(
                id = dailyTime.id,
                date = dailyTime.recordDate,
                weather = dailyTime.weather.dbName,
                logs = logEntries.toMutableList()
            )
        }

        return PageVO<DailyLogVO>().apply {
            total = dailyTimePage.total
            rows = dailyLogVOs
        }
    }
}