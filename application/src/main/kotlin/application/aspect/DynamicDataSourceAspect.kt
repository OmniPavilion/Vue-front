package application.aspect

import common.annotation.Datasource
import application.bean.DynamicDataSource
import jakarta.annotation.PostConstruct
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.Locale.getDefault

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // 确保先于事务切面执行
class DynamicDataSourceAspect {
    private val logger = mu.KotlinLogging.logger {}

    // 修改为拦截类上的注解
    @Before("@within(datasource)")
    fun changeDataSource(datasource: Datasource) {
        val dsName = datasource.value.name.lowercase()
        logger.info { "!!! 切面生效，切换数据源到: $dsName" } // 添加特殊标记便于日志搜索
        DynamicDataSource.setCurrentDataSource(dsName)
    }

    @PostConstruct
     fun init() {
        logger.info("!!! 切面初始化完成")
    }
}

