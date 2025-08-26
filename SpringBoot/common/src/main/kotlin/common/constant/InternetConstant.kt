package common.constant

import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate


/**
 * @author 彭超
 * @version 1.0
 * @description 网络配置
 * @date 2025-08-24 19:02
 */
@Component
@ConfigurationProperties(prefix = "internet")
class InternetConstant {
    var PORT: String = "8080"
    var IPS: List<String> = emptyList() // 改为List类型接收YAML数组
    var url: String = ""

    // 创建带超时的 RestTemplate
    private val restTemplate: RestTemplate by lazy {
        val factory = SimpleClientHttpRequestFactory()
        factory.setConnectTimeout(500) // 5秒连接超时
        factory.setReadTimeout(100)    // 3秒读取超时
        RestTemplate(factory)
    }

    @PostConstruct
    fun init() {
        selectAvailableUrl()
    }

    // 每30秒检查一次网络状态
    @Scheduled(fixedRate = 30000)
    fun selectAvailableUrl() {
        val availableUrl = findAvailableUrl()
        this.url = "$availableUrl$PORT"
        println("🎯 最终选择的服务器地址: ${this.url}")
    }

    private fun findAvailableUrl(): String {
        // 解析IP列表
        val urlList = parseUrls()
        if (urlList.isEmpty()) {
            return "http://localhost"
        }

        // 检查每个地址的可用性
        for (testUrl in urlList) {
            if (checkUrlAvailability(testUrl)) {
                println("✅ 使用服务器地址: $testUrl")
                return testUrl
            }
            println("❌ 地址不可用: $testUrl")
        }

        // 所有地址都不可用时使用第一个
        val fallbackUrl = urlList.last()
        println("⚠️ 所有地址都不可用，使用备用地址: $fallbackUrl")
        return fallbackUrl
    }

    private fun parseUrls(): List<String> {
        return if (IPS.isEmpty()) {
            listOf("http://localhost")
        } else {
            IPS.map { url ->
                // 处理配置中的地址格式
                if (url.endsWith(":")) {
                    url // 如果以冒号结尾，添加端口
                } else if (!url.contains("://")) {
                    "https://$url" // 如果没有协议，添加http://和端口
                } else {
                    url // 已经是完整URL
                }
            }
        }
    }

    private fun checkUrlAvailability(url: String): Boolean {
        return try {
            // 尝试发送HEAD请求检查连通性
            restTemplate.headForHeaders("$url/actuator/health")
            true
        } catch (e: Exception) {
            // 如果健康检查端点不存在，尝试基础路径
            try {
                restTemplate.headForHeaders(url)
                true
            } catch (e2: Exception) {
                // 记录详细的错误信息
                when {
                    e2.message?.contains("Connection refused") == true ->
                        println("连接被拒绝: $url")
                    e2.message?.contains("connect timed out") == true ->
                        println("连接超时: $url")
                    else ->
                        println("检查失败: $url - ${e2.message}")
                }
                false
            }
        }
    }

    fun getAvailableUrl(): String = url
}