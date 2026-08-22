package com.chuncongcong.sw.security;

import com.chuncongcong.framework.interceptor.UserStatusChecker;
import com.chuncongcong.sw.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class SwUserStatusChecker implements UserStatusChecker {

    @Resource
    private UserService userService;

    @Override
    public boolean isActive(Long userId) {
        return userId != null && userService.getById(userId) != null;
    }
}
