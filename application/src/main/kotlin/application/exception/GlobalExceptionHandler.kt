package application.exception

import diary.exception.DailyException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import common.pojo.vo.Result
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes

@RestControllerAdvice
class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DailyException::class)
    fun handleNotFound(e: DailyException): Result<Unit> {
        return Result.error(e.message ?: "Not Found")
    }

    @Bean
    fun errorAttributes(): ErrorAttributes {
        return DefaultErrorAttributes().apply {
        }
    }
}