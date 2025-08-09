package bibliophile.service.impl

import AiReaderFile
import bibliophile.constant.AiReaderFileConstant
import bibliophile.constant.FIlePathConstant
import bibliophile.exception.AiReaderFileException
import bibliophile.mapper.AiReaderFileMapper
import bibliophile.service.AiReaderFileService
import common.annotation.Datasource
import common.enumerate.DataSourceType
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import common.constant.InternetConstant
import common.exception.FileException
import common.utils.MultipartFileUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.concurrent.ThreadLocalRandom

@Service
@Datasource(DataSourceType.BIBLIOPHILE)
class AiReaderFileServiceImpl(
    private val aiReaderFileMapper: AiReaderFileMapper,
    private val aiReaderFileConstant: AiReaderFileConstant,
    private val internetConstant: InternetConstant
) : AiReaderFileService {

    @Transactional
    override fun createFile(file: MultipartFile): Long {
        // 1. 验证文件基本属性
        if (file.isEmpty) {
            throw AiReaderFileException("上传文件不能为空")
        }

        val title = file.originalFilename!!.substringBeforeLast(".")
        val fileSize = file.size
        val extension = file.originalFilename?.substringAfterLast(".")
        val fileName = System.currentTimeMillis().toString() + "." + extension

        // 2. 使用工具类处理文件存储
        val tempFile = try {
            MultipartFileUtils.getFileByMultipartFile(file)
        } catch (e: FileException) {
            throw AiReaderFileException("文件处理失败: ${e.message}")
        }

        // 3. 生成最终存储路径
        val path = "${aiReaderFileConstant.rootPath}/${fileName}"

        // 4. 将文件移动到最终位置
        try {
            MultipartFileUtils.addFile(tempFile, path)
        } catch (e: FileException) {
            tempFile.delete()
            throw AiReaderFileException("文件存储失败: ${e.message}")
        } finally {
            tempFile.delete() // 确保临时文件被清理
        }

        // 5. 创建文件记录
        val now = LocalDateTime.now()

        val po = AiReaderFile(
            title = title,
            fileSize = fileSize,
            createdAt = now,
            updatedAt = now,
            fileName = fileName,
        )

        if (aiReaderFileMapper.insert(po) != 1) {
            // 如果数据库插入失败，删除已存储的文件
            MultipartFileUtils.deleteFolderSafely(path)
            throw AiReaderFileException("创建文件记录失败")
        }

        return po.id ?: throw AiReaderFileException("获取文件ID失败")
    }

    override fun getFileById(id: Long): AiReaderFile {
        val po = aiReaderFileMapper.selectById(id) ?: throw AiReaderFileException("未找到ID为${id}的文件")
        val url = internetConstant.url
        po.url = "$url${FIlePathConstant.AI_READER_FILE_STATIC_PATH}${po.fileName}"
        return po
    }


    override fun deleteFile(id: Long) {
        if (aiReaderFileMapper.selectCount(
                KtQueryWrapper(AiReaderFile::class.java)
                    .eq(AiReaderFile::id, id)
            ) == 0L
        ) {
            throw AiReaderFileException("未找到ID为${id}的文件")
        }

        MultipartFileUtils.deleteFile("${aiReaderFileConstant.rootPath}/${getFileById(id).fileName}")

        if (aiReaderFileMapper.deleteById(id) != 1) {
            throw AiReaderFileException("删除文件记录失败")
        }

    }

    override fun getAllFiles(): List<AiReaderFile> {
        val selectList = aiReaderFileMapper.selectList(null)
        for (po in selectList) {
            val url = internetConstant.url
            po.url = "$url${FIlePathConstant.AI_READER_FILE_STATIC_PATH}${po.fileName}"
        }
        return selectList
    }
}