package application.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@RequiredArgsConstructor
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    /**
     * 请求处理前的拦截逻辑
     * @param request 当前HTTP请求
     * @param response 当前HTTP响应
     * @param handler 被调用的处理器对象
     * @return true表示继续执行请求处理链，false表示中断请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        log.info("LoginInterceptor 登录拦截器");
        // 获取当前请求的URI
        String requestURI = request.getRequestURI();
        log.info("当前请求URI:{}", requestURI);
        return true;
    }
}