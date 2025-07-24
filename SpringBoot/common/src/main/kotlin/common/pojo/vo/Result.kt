package common.pojo.vo

class Result<T> {

    var code: Int = 0       // 状态码
    var message: String? = null // 消息
    var data: T? = null     // 数据

    constructor(code: Int, message: String, data: T?) {
        this.code = code
        this.message = message
        this.data = data
    }

    companion object {
        fun success(): Result<Unit> {
            return Result(1, "success", Unit)
        }

        fun <T> success(data: T): Result<T> {
            return Result(1, "success", data)
        }

        fun error(message: String): Result<Unit> {
            return Result(-1, message, Unit)
        }
    }
}