package com.chuncongcong.framework.interceptor;

/**
 * Provides application-specific validation for the user represented by a JWT.
 */
@FunctionalInterface
public interface UserStatusChecker {

    boolean isActive(Long userId);
}
