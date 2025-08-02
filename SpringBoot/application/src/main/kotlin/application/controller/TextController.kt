package application.controller

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.pojo.vo.Result
import mu.KotlinLogging
import music.pojo.vo.CategoryVO
import music.service.CategoryService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/text")
class TextController() {
    @RequestMapping()
    fun text(): Result<String> {
       return Result.success("连接成功！！！")
    }
}