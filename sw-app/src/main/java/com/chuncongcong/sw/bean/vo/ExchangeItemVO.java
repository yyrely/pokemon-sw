package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExchangeItemVO {

    @Schema(description = "collect record id")
    private Long collectId;

    @Schema(description = "user id")
    private Long userId;

    @Schema(description = "item id")
    private Long itemId;

    @Schema(description = "source type: 1 main item, 2 sub item, 3 pokemon collect")
    private Integer sourceType;

    @Schema(description = "sub item id json")
    private String subItemId;

    @Schema(description = "item name")
    private String itemName;

    @Schema(description = "item image")
    private String itemImg;

    @Schema(description = "collect date")
    private LocalDate collectDate;

    @Schema(description = "collect price")
    private BigDecimal collectPrice;

    @Schema(description = "remark")
    private String remark;

    @Schema(description = "exchange remark")
    private String exchangeRemark;

    @Schema(description = "exchange")
    private Boolean exchange;
}
