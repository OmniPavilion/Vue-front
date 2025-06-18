package springBoot.config


import application.interceptor.LoginInterceptor
import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@RequiredArgsConstructor
@Configuration
class MvcConfiguration : WebMvcConfigurer {
    private val loginInterceptor: LoginInterceptor? = null

    // 用于添加跨源资源共享（CORS）的映射
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") // 所有的请求路径（  /**  ）应用 CORS 配置。
            .allowedOrigins("*") // 许所有来源（  *  ）的请求
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许上述 HTTP 方法。
    }

    // 配置拦截器
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(loginInterceptor!!)
            .addPathPatterns("/**")
            .excludePathPatterns("/login")
    }
}