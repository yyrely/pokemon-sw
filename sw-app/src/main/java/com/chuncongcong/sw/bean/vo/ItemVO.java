package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "物品信息")
public class ItemVO {

    @Schema(description = "物品ID")
    private Long id;

    @Schema(description = "区域ID")
    private Long regionId;

    @Schema(description = "物品名称")
    private String name;

    @Schema(description = "物品编码")
    private String code;

    @Schema(description = "物品图片URL")
    private String img;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "是否收集")
    private Boolean collect = Boolean.FALSE;

    @Schema(description = "收集最低价")
    private BigDecimal minCollectPrice;


}
