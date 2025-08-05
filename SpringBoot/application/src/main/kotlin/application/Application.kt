package application

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(scanBasePackages = ["application", "diary",  "common", "music", "article"])
@MapperScan(value = ["diary.mapper", "music.mapper", "article.mapper", "common.mapper"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
