package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "物品信息")
public class ItemInfoVO {

    @Schema(description = "物品ID")
    private Long itemId;

    @Schema(description = "时间")
    private LocalDate date;

    @Schema(description = "类型，1-发售，2-再版，3-定番")
    private Integer type;

    @Schema(description = "价格")
    private String price;
}
