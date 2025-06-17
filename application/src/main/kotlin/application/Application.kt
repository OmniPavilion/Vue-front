package application

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(scanBasePackages = ["application", "diary",  "common", "music"])
@MapperScan("diary.mapper")
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
