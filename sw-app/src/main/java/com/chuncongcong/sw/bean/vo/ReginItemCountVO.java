package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "地区统计信息")
public class ReginItemCountVO {

    @Schema(description = "区域ID")
    private Long regionId;

    @Schema(description = "物品总数")
    private Integer count = 0;

    @Schema(description = "物品已收集数量")
    private Integer collectCount = 0;

    @Schema(description = "物品未收集数量")
    private Integer unCollectCount = 0;

    @Schema(description = "物品已收集比例")
    private BigDecimal collectRate = BigDecimal.ZERO;

    @Schema(description = "物品收集总数")
    private Integer collectNumber = 0;

    @Schema(description = "物品已收集金额")
    private BigDecimal collectPriceTotal = BigDecimal.ZERO;

}
