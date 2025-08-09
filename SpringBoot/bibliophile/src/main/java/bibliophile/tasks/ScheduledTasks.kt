package bibliophile.tasks


import bibliophile.service.AiReaderFileAiMemoryService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduledTasks(
    private val aiReaderFileAiMemoryService: AiReaderFileAiMemoryService
) {
    @Scheduled(fixedRate = 1000 * 60 * 60)
    fun taskWithFixedRate() {
        aiReaderFileAiMemoryService.deleteVectorStore()
    }
}