package application.aspect

import common.annotation.Datasource
import application.bean.DynamicDataSource
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import java.util.Locale.getDefault

@Component
@Aspect
class DynamicDAtaSourceAspect {
    @Before("@annotation(datasource)")
    fun before(datasource: Datasource) {
        DynamicDataSource.setCurrentDataSource(datasource.value.name.lowercase(getDefault()))
    }
}