package article.config

import article.Property.DataSourceProperty
import article.advise.ArticleAdvisor
import article.constant.PromptConstant
import article.pojo.po.Article
import com.zaxxer.hikari.HikariDataSource
import common.annotation.Datasource
import common.enumerate.DataSourceType
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.SQLException
import javax.sql.DataSource


@Configuration
class ChatClientConfig(
    private val dataSourceProperty: DataSourceProperty
) {
    @Bean
    fun chatMemoryRepository(): JdbcChatMemoryRepository {
        // 创建独立的数据源，避免被动态切换干扰
        val articleDataSource = DataSourceBuilder.create()
            .driverClassName(dataSourceProperty.driverClassName)
            .url(dataSourceProperty.jdbcUrl)
            .username(dataSourceProperty.username)
            .password(dataSourceProperty.password)
            .type(HikariDataSource::class.java)
            .build()

        val jdbcTemplate = JdbcTemplate(articleDataSource)
        return JdbcChatMemoryRepository.builder()
            .jdbcTemplate(jdbcTemplate)
            .build()
    }

    @Bean
    fun mySqlChatMemory(chatMemoryRepository: JdbcChatMemoryRepository): ChatMemory {
        return MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(9999) // 保留最近10条消息
            .build()
    }

    @Bean
    fun articleChatClient(chatModel: OpenAiChatModel, mySqlChatMemory: ChatMemory): ChatClient {
        return ChatClient
            .builder(chatModel)
            .defaultSystem(PromptConstant.ARTICLE_PROMPT)
            .defaultAdvisors(
                SimpleLoggerAdvisor(), // 日志顾问
                MessageChatMemoryAdvisor.builder(mySqlChatMemory).build(), // 聊天记忆顾问
                ArticleAdvisor() // 文章咨询顾问
            )
            .build()
    }
}