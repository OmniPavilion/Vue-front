package music.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import music.pojo.po.Music
import org.apache.ibatis.annotations.Mapper

@Mapper
interface MusicMapper : BaseMapper<Music>