package bibliophile.config

import bibliophile.constant.PromptConstant
import bibliophile.property.BibliophileDataSourceProperty
import com.zaxxer.hikari.HikariDataSource
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.SimpleVectorStore
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate


@Configuration
class BibliophileChatClientConfig(
    private val bibliophileDataSourceProperty: BibliophileDataSourceProperty
) {
    @Bean
    fun bibliophileChatMemoryRepository(): JdbcChatMemoryRepository {
        // 创建独立的数据源，避免被动态切换干扰
        val articleDataSource = DataSourceBuilder.create()
            .driverClassName(bibliophileDataSourceProperty.driverClassName)
            .url(bibliophileDataSourceProperty.jdbcUrl)
            .username(bibliophileDataSourceProperty.username)
            .password(bibliophileDataSourceProperty.password)
            .type(HikariDataSource::class.java)
            .build()

        val jdbcTemplate = JdbcTemplate(articleDataSource)
        return JdbcChatMemoryRepository.builder()
            .jdbcTemplate(jdbcTemplate)
            .build()
    }

    @Bean
    fun bibliophileMySqlChatMemory(bibliophileChatMemoryRepository: JdbcChatMemoryRepository): ChatMemory {
        return MessageWindowChatMemory.builder()
            .chatMemoryRepository(bibliophileChatMemoryRepository)
            .maxMessages(9999) // 保留最近10条消息
            .build()
    }

    // 向量数据库
    @Bean
    fun bibliophileVectorStore(model: OpenAiEmbeddingModel): VectorStore {
        return SimpleVectorStore.builder(model).build()
    }

    @Bean
    fun bibliophileChatClient(
        chatModel: OpenAiChatModel,
        bibliophileMySqlChatMemory: ChatMemory,
        bibliophileVectorStore: VectorStore): ChatClient {
        return ChatClient
            .builder(chatModel)
            .defaultSystem(PromptConstant.ARTICLE_PROMPT)
            .defaultAdvisors(
                SimpleLoggerAdvisor(), // 日志顾问
                MessageChatMemoryAdvisor.builder(bibliophileMySqlChatMemory).build(), // 聊天记忆顾问
                // 消息记忆顾问，用于储存聊天记录
                QuestionAnswerAdvisor.builder(bibliophileVectorStore) // 配置向量模型
                    .searchRequest(
                        SearchRequest.builder()
                            .similarityThreshold(0.3) //  相似度阈值
                            .topK(5) //  返回的相似度最高的结果数量
                            .build()
                    )
                    .build()
            )
            .build()
    }
}