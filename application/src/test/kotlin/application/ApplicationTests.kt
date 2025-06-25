package application

import com.fasterxml.jackson.databind.ObjectMapper
import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.enumerate.SortDirection
import common.pojo.dto.PageDTO
import common.pojo.vo.Result
import mu.KotlinLogging
import music.mapper.MusicMapper
import music.service.CategoryService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.redis.core.StringRedisTemplate

@SpringBootTest
class ApplicationTests{

    @Autowired
    private lateinit var categoryService: CategoryService

    private val logger = KotlinLogging.logger {}


    @Test
    fun contextLoads() {
//        val pageDto = PageDTO<String>()
//        pageDto.pageNum = 1
//        pageDto.pageSize = 10
//        pageDto.order = SortDirection.ASC
//        pageDto.query = "流行"
//        val result = categoryService.getCategoryPage(pageDto)

        val result = categoryService.getCategoryById(1)
        logger.info { "result: $result" }
    }
}