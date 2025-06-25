package application.bean

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
@Primary
class DynamicDataSource : AbstractRoutingDataSource() {
    @Autowired
    private lateinit var dataSourceInfoBean: DataSourceInfoBean

    companion object {
        private val contextHolder = ThreadLocal<String>()

        fun setCurrentDataSource(dataSourceName: String) {
            contextHolder.set(dataSourceName.lowercase())
        }

        fun clearCurrentDataSource() {
            contextHolder.remove()
        }
    }

    override fun determineCurrentLookupKey(): Any? {
        val key = contextHolder.get()
        logger.info("当前数据源: $key") // 添加这行
        return key
    }

    override fun afterPropertiesSet() {
        val targetDataSources = HashMap<Any, Any>()
        logger.info("数据源配置信息: ${JSON.toJSONString(dataSourceInfoBean.datasource, SerializerFeature.PrettyFormat)}")

        dataSourceInfoBean.datasource.forEach { (dataSourceName, config) ->
            val ds = createDataSource(config)
            targetDataSources[dataSourceName] = ds
            logger.info("已初始化数据源: $dataSourceName")
        }

        if (targetDataSources.isEmpty()) {
            throw IllegalStateException("未配置任何数据源")
        }

        super.setTargetDataSources(targetDataSources)
        super.setDefaultTargetDataSource(targetDataSources.values.first())
        super.afterPropertiesSet()
    }

    private fun createDataSource(config: Map<String, Any>): DataSource {
        return DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .url(config["jdbc-url"].toString())
            .username(config["username"].toString())
            .password(config["password"].toString())
            .driverClassName(config["driver-class-name"].toString())
            .build()
            .also {
                if (it is HikariDataSource) {
                    it.maximumPoolSize = 10
                    it.connectionTimeout = 30000
                }
            }
    }
}