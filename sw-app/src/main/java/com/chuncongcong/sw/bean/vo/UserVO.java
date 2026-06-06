package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "username")
    private String username;

    @Schema(description = "avatar")
    private String headImgUrl;

    @Schema(description = "contact")
    private String contact;
}
