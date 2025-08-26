package bibliophile.initializer


import bibliophile.service.AiReaderFileAiMemoryService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class StartupInitializer(
    private val aiReaderFileAiMemoryService: AiReaderFileAiMemoryService
) {
    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        // 应用完全启动后执行
        println("应用已启动，执行初始化操作...")
        // 调用你的函数
        init()
    }

    private fun init() {
        aiReaderFileAiMemoryService.deleteVectorStore()
    }
}