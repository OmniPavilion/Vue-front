package article.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring.datasource.article")
class ArticleDataSourceProperty {
    var jdbcUrl: String? = null
    var username: String? = null
    var password: String? = null
    var driverClassName: String? = null
    var type: String? = null
}