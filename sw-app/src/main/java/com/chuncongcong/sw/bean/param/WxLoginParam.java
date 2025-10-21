package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WxLoginParam {

    @NotBlank(message = "code不能为空")
    @Schema(description = "微信登录code")
    private String code;
}
