package application

import common.annotation.Datasource
import common.enumerate.DataSourceType
import diary.mapper.PlanTaskMapper
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ApplicationTests {
    @Autowired
    private lateinit var noteMapper: PlanTaskMapper // 使用 lateinit 而非可空类型
    private val log = KotlinLogging.logger {}

    @Test
    @Datasource(DataSourceType.DIARY)
    fun contextLoads() {
        val notes = noteMapper.selectList(null)
        log.info { "查询到的笔记: $notes" }
    }
}