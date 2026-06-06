package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserParam {

    @NotNull(message = "user id cannot be empty")
    @Schema(description = "user id")
    private Long id;

    @NotBlank(message = "username cannot be empty")
    @Size(min = 1, max = 10, message = "username length must be between 1 and 10")
    @Pattern(regexp = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$", message = "username contains invalid characters")
    @Schema(description = "username")
    private String username;

    @Schema(description = "gender")
    private Integer gender;

    @Schema(description = "mobile")
    private String mobile;

    @Schema(description = "contact")
    private String contact;

    @Schema(description = "avatar")
    private String headImgUrl;
}
