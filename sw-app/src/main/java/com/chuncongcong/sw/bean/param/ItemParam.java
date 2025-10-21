package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ItemParam {

    @Schema(description = "物品ID")
    private Long id;

    @Schema(description = "区域ID")
    private Long regionId;
}
