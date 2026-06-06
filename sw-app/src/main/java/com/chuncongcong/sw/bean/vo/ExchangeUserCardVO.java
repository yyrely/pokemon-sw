package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ExchangeUserCardVO {

    @Schema(description = "user id")
    private Long userId;

    @Schema(description = "username")
    private String username;

    @Schema(description = "avatar")
    private String headImgUrl;

    @Schema(description = "contact")
    private String contact;

    @Schema(description = "exchange item count")
    private Integer exchangeCount;

    @Schema(description = "preview items")
    private List<ExchangeItemVO> itemList;
}
