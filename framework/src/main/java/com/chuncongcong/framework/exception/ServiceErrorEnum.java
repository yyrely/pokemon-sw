package com.chuncongcong.framework.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceErrorEnum {

    TOKEN_ERROR(401, "无效认证，请重新登录"),

    PARAM_ERROR(400, "参数错误"),
    ;

    private final int code;

    private final String desc;
}
