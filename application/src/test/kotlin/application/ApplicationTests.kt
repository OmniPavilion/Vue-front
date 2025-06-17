package application

import application.annotation.Datasource
import application.enumerate.DataSourceType
import diary.mapper.PlanTaskMapper
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ApplicationTests {
    @Autowired // 改用字段注入
    private lateinit var noteMapper: PlanTaskMapper
    private val log = KotlinLogging.logger {}

    @Test
    @Datasource(DataSourceType.DIARY)
    fun contextLoads() {
        // 查询所有笔记
        val notes = noteMapper.selectList(null)
        log.info { notes }
    }
}
