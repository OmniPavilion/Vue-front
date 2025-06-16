package diary.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DiaryDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.diary") // 规范前缀格式
    fun diaryDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }
}