package bibliophile.service.impl

import AiReaderFile
import bibliophile.constant.AiReaderFileConstant
import bibliophile.exception.AiReaderFileException
import bibliophile.mapper.AiReaderFileMapper
import bibliophile.service.AiReaderFileAiMemoryService
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.mapper.AiMemoryMapper
import common.service.impl.AiMemoryServiceImpl
import common.utils.MultipartFileUtils
import common.utils.ResourceUtils
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

@Service
@Datasource(DataSourceType.BIBLIOPHILE)
class AiReaderFileAiMemoryServiceImpl(
    aiMemoryMapper: AiMemoryMapper,
    private val aiReaderFileMapper: AiReaderFileMapper,
    private val aiReaderFileConstant: AiReaderFileConstant,
    private val bibliophileVectorStore: VectorStore
): AiReaderFileAiMemoryService, AiMemoryServiceImpl(aiMemoryMapper) {

    private val docMap = mutableMapOf<Long, List<String>>()
    override fun setFileIntoVectorStore(fileId: Long) {
        val aiReaderFile = aiReaderFileMapper.selectById(fileId)
        if (aiReaderFile == null) {
            throw AiReaderFileException("文件不存在")
        }

        if (aiReaderFile.aiProcessed == 0) {
            return
        }
        aiReaderFile.aiProcessed = 0

        val path = aiReaderFileConstant.rootPath + "/" + aiReaderFile.fileName

        val file = MultipartFileUtils.getFile(path)

        val documents = ResourceUtils.parseResourceToDocuments(file)
        // 打印 文档
        for (document in documents) {
            println("""
        === Document ===
        Content: ${document.text}
        Metadata: ${document.metadata}
        ================
    """.trimIndent())
        }

        bibliophileVectorStore.add(documents)
        val ids = documents.map { it.id }
        docMap[fileId] = ids
        aiReaderFileMapper.updateById(aiReaderFile)

    }

    override fun deleteVectorStore() {
        val apply = KtUpdateWrapper(AiReaderFile::class.java).apply {
            set(AiReaderFile::aiProcessed, 0)
            update()
        }
        aiReaderFileMapper.update( apply)
        docMap.forEach { (_, ids) ->
            bibliophileVectorStore.delete(ids)
        }
        docMap.clear()
    }

    override fun deleteVectorStoreByFileId(fileId: Long) {
        val ids = docMap[fileId]
        if (ids != null) {
            bibliophileVectorStore.delete(ids)
        } else {
            throw AiReaderFileException("文件不存在")
        }
    }
}