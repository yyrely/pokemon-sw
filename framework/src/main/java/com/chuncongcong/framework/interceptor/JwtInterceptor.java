package com.chuncongcong.framework.interceptor;

import com.chuncongcong.framework.exception.ServiceErrorEnum;
import com.chuncongcong.framework.exception.ServiceException;
import com.chuncongcong.framework.util.ContextHolder;
import com.chuncongcong.framework.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION = "Authorization";

    private static final String TOKEN_PREFIX = "Bearer ";

    private final UserStatusChecker userStatusChecker;

    public JwtInterceptor(UserStatusChecker userStatusChecker) {
        this.userStatusChecker = userStatusChecker;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 判断 handler 是否为方法
        // 方法级注解
        IgnoreLogin methodAnnotation = handlerMethod.getMethodAnnotation(IgnoreLogin.class);
        // 类级注解
        IgnoreLogin classAnnotation = handlerMethod.getBeanType().getAnnotation(IgnoreLogin.class);
        if (methodAnnotation != null || classAnnotation != null) {
            String token = request.getHeader(AUTHORIZATION);
            if (token != null && token.startsWith(TOKEN_PREFIX)) {
                Long userId = JwtUtil.parseToken(token.substring(7));
                if (userId != null) {
                    assertUserActive(userId);
                    ContextHolder.setUserId(userId);
                }
            }
            // 放行
            return true;
        }
        // 从 header 获取 token
        String token = request.getHeader(AUTHORIZATION);
        if (token == null || !token.startsWith(TOKEN_PREFIX)) {
            throw ServiceException.instance(ServiceErrorEnum.TOKEN_ERROR);
        }
        // 去掉 "Bearer "
        token = token.substring(7);
        Long userId = JwtUtil.parseToken(token);
        if (userId == null) {
            throw ServiceException.instance(ServiceErrorEnum.TOKEN_ERROR);
        }

        assertUserActive(userId);
        ContextHolder.setUserId(userId);
        return true;
    }

    private void assertUserActive(Long userId) {
        if (!userStatusChecker.isActive(userId)) {
            throw ServiceException.instance(ServiceErrorEnum.TOKEN_ERROR);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ContextHolder.clear();
    }
}
