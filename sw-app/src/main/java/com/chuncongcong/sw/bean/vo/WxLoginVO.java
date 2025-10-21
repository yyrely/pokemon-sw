package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WxLoginVO {

    @Schema(description = "登录token")
    private String token;

    @Schema(description = "用户信息")
    private UserVO user;
}
