package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "排行信息")
public class TopVO {

    private Long userId;

    private String username;

    private String headImgUrl;

    private Integer collectCount;

    private Integer collectPrice;
}
