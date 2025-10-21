package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserParam {

    @NotNull(message = "用户id不能为空")
    @Schema(description = "用户id")
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "手机号")
    private String mobile;

    @NotBlank(message = "头像不能为空")
    @Schema(description = "头像")
    private String headImgUrl;
}
