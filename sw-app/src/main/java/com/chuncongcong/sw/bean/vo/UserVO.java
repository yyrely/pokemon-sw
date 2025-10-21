package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserVO {

    @Schema(description = "登录token")
    private Long id;

    @Schema(description = "用户名")
    private String username;

}
