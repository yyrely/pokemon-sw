package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ExchangeSearchParam {

    @Schema(description = "keyword")
    private String keyword;

    @Schema(description = "page number")
    private Integer pageNo = 1;

    @Schema(description = "page size")
    private Integer pageSize = 3;

    @Schema(description = "user id")
    private Long userId;
}
