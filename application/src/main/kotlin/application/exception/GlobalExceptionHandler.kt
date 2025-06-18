package application.exception

import common.exception.BusinessException
import diary.exception.DailyException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import common.pojo.vo.Result
import diary.exception.NoteException
import org.apache.ibatis.javassist.NotFoundException
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes

@RestControllerAdvice
class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BusinessException::class)
    fun handleNoteException(e: BusinessException): Result<Unit> {
        return Result.error(e.message ?: "Not Found")
    }

    @Bean
    fun errorAttributes(): ErrorAttributes {
        return DefaultErrorAttributes().apply {
        }
    }
}