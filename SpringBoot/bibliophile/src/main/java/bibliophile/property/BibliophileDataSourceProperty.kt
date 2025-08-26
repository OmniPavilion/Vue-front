package bibliophile.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring.datasource.bibliophile")
class BibliophileDataSourceProperty {
    var jdbcUrl: String? = null
    var username: String? = null
    var password: String? = null
    var driverClassName: String? = null
    var type: String? = null
}