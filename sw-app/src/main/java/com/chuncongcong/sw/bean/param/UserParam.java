package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserParam {

    @NotNull(message = "用户id不能为空")
    @Schema(description = "用户id")
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 10, message = "用户名长度必须在1到10个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$", message = "用户名包含非法字符，只能包含中文、英文、数字或下划线")
    @Schema(description = "用户名")
    private String username;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "头像")
    private String headImgUrl;
}
