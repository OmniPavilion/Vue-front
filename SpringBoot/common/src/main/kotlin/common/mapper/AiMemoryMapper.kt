package common.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import common.pojo.po.SpringAiChatMemory
import org.apache.ibatis.annotations.Mapper

@Mapper
interface AiMemoryMapper: BaseMapper<SpringAiChatMemory>