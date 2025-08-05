package article.advise

import org.springframework.ai.chat.client.ChatClientRequest
import org.springframework.ai.chat.client.ChatClientResponse
import org.springframework.ai.chat.client.advisor.api.CallAdvisor
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.core.Ordered
import org.springframework.util.Assert
import reactor.core.publisher.Flux
import org.slf4j.LoggerFactory

class ArticleAdvisor : CallAdvisor, StreamAdvisor {
    companion object {
        const val ARTICLE_CONTEXT = "article_context"
        const val ARTICLE_TITLE = "article_TITLE"
        private val logger = LoggerFactory.getLogger(ArticleAdvisor::class.java)
        const val ADVISOR_ORDER = 0 // 执行顺序，数值越小优先级越高
    }

    override fun getName(): String {
        return "文章内容顾问" // 中文名称
    }

    override fun getOrder(): Int {
        return ADVISOR_ORDER
    }

    override fun adviseCall(
        chatClientRequest: ChatClientRequest,
        callAdvisorChain: CallAdvisorChain
    ): ChatClientResponse {
        logger.debug("开始处理带文章内容的同步请求")

        val modifiedRequest = try {
            processRequest(chatClientRequest)
        } catch (e: Exception) {
            logger.error("处理请求时发生异常", e)
            throw e
        }

        val response = callAdvisorChain.nextCall(modifiedRequest)

        logger.debug("同步请求处理完成")
        return response
    }

    override fun adviseStream(
        chatClientRequest: ChatClientRequest,
        streamAdvisorChain: StreamAdvisorChain
    ): Flux<ChatClientResponse?> {
        logger.debug("开始处理带文章内容的流式请求")

        return try {
            val modifiedRequest = processRequest(chatClientRequest)
            streamAdvisorChain.nextStream(modifiedRequest)
                .doOnError { e ->
                    logger.error("流式处理过程中发生错误", e)
                }
                .doOnComplete {
                    logger.debug("流式请求处理完成")
                }
        } catch (e: Exception) {
            logger.error("处理流式请求时发生异常", e)
            Flux.error(e)
        }
    }

    private fun processRequest(chatClientRequest: ChatClientRequest): ChatClientRequest {
        // 1. 验证并获取文章内容
        val article = chatClientRequest.context()[ARTICLE_CONTEXT]?.toString()
            ?: run {
                logger.error("请求中缺少文章内容(${ARTICLE_CONTEXT})")
                throw IllegalArgumentException("请求中必须包含文章内容")
            }

        if (article.isBlank()) {
            logger.warn("获取到的文章内容为空")
        }

        val title = chatClientRequest.context()[ARTICLE_TITLE]?.toString()
            ?: run {
                logger.error("请求中缺少文章标题(${ARTICLE_TITLE})")
                throw IllegalArgumentException("请求中必须包含文章标题")
            }

        if (title.isBlank()) {
            logger.warn("获取到的文章标题为空")
        }

        // 2. 获取原始用户提问
        val originalPrompt = chatClientRequest.prompt().instructions.joinToString("\n")
        if (originalPrompt.isBlank()) {
            logger.warn("用户提问内容为空")
        }

        logger.debug("原始用户提问内容：{}", originalPrompt)

        // 3. 构建结合文章的新提示词
        val newPrompt = """
            |【文章标题】
            |${title}
            |
            |【文章内容】
            |$article
            |
            |【用户提问】
            |$originalPrompt
            |
            |请根据上述文章内容回答用户问题。
            """.trimMargin()

        logger.debug("生成的新提示词：\n{}", newPrompt)

        // 4. 创建消息列表
        val messages = listOf(UserMessage(newPrompt))

        // 5. 构建修改后的请求
        return chatClientRequest.mutate()
            .prompt(chatClientRequest.prompt().mutate().messages(messages).build())
            .build()
    }
}