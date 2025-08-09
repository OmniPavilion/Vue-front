package bibliophile.service

import common.service.AiMemoryService

interface AiReaderFileAiMemoryService : AiMemoryService {
    fun setFileIntoVectorStore(fileId: Long)
    fun deleteVectorStore()
    fun deleteVectorStoreByFileId(fileId: Long)
}