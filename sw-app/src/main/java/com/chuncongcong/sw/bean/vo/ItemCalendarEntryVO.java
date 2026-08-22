package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "新品日历条目")
public class ItemCalendarEntryVO {

    @Schema(description = "物品ID")
    private Long itemId;

    @Schema(description = "物品名称")
    private String itemName;

    @Schema(description = "物品图片URL")
    private String itemImg;

    @Schema(description = "区域ID")
    private Long regionId;

    @Schema(description = "日期")
    private LocalDate date;

    @Schema(description = "类型，1-发售，2-再版，3-定番")
    private Integer type;

    @Schema(description = "价格")
    private String price;
}
