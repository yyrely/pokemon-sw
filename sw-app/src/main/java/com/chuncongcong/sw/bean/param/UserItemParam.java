package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "用户物品信息")
public class UserItemParam {

    @Schema(description = "用户物品ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "项目类型，1-主物品，2-子物品")
    private Integer itemType;

    @Schema(description = "物品ID")
    private Long itemId;

    @Schema(description = "子物品ID")
    private Long subItemId;

    @Schema(description = "收集时间")
    private LocalDate collectDate;

    @Schema(description = "收集价格")
    private BigDecimal collectPrice;

    @Schema(description = "备注")
    private String remark;

}
