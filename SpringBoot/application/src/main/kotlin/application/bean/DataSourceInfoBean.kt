package application.bean

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


@Component
@ConfigurationProperties(prefix = "spring")
class DataSourceInfoBean {
    var datasource: MutableMap<String, MutableMap<String, Any>> = mutableMapOf()
}
