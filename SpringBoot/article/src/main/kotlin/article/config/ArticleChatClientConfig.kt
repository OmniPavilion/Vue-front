package article.config

import article.advise.ArticleAdvisor
import article.constant.PromptConstant
import article.property.ArticleDataSourceProperty
import com.zaxxer.hikari.HikariDataSource
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


@Configuration
class ArticleChatClientConfig(
    private val articleDataSourceProperty: ArticleDataSourceProperty
) {
    @Bean
    fun articleChatMemoryRepository(): JdbcChatMemoryRepository {
        // 创建独立的数据源，避免被动态切换干扰
        val articleDataSource = DataSourceBuilder.create()
            .driverClassName(articleDataSourceProperty.driverClassName)
            .url(articleDataSourceProperty.jdbcUrl)
            .username(articleDataSourceProperty.username)
            .password(articleDataSourceProperty.password)
            .type(HikariDataSource::class.java)
            .build()

        val jdbcTemplate = JdbcTemplate(articleDataSource)
        return JdbcChatMemoryRepository.builder()
            .jdbcTemplate(jdbcTemplate)
            .build()
    }

    @Bean
    fun articleMySqlChatMemory(articleChatMemoryRepository: JdbcChatMemoryRepository): ChatMemory {
        return MessageWindowChatMemory.builder()
            .chatMemoryRepository(articleChatMemoryRepository)
            .maxMessages(9999) // 保留最近10条消息
            .build()
    }

    @Bean
    fun articleChatClient(chatModel: OpenAiChatModel, articleMySqlChatMemory: ChatMemory): ChatClient {
        return ChatClient
            .builder(chatModel)
            .defaultSystem(PromptConstant.ARTICLE_PROMPT)
            .defaultAdvisors(
                SimpleLoggerAdvisor(), // 日志顾问
                MessageChatMemoryAdvisor.builder(articleMySqlChatMemory).build(), // 聊天记忆顾问
                ArticleAdvisor() // 文章咨询顾问
            )
            .build()
    }
}