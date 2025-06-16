package application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(scanBasePackages = ["application", "diary",  "common"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
