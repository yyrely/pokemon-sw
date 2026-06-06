package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "user item")
public class UserItemVO {

    @Schema(description = "collect record id")
    private Long id;

    @Schema(description = "user id")
    private Long userId;

    @Schema(description = "item id")
    private Long itemId;

    @Schema(description = "sub item id")
    private String subItemId;

    @Schema(description = "name")
    private String name;

    @Schema(description = "collect date")
    private LocalDate collectDate;

    @Schema(description = "collect price")
    private BigDecimal collectPrice;

    @Schema(description = "remark")
    private String remark;

    @Schema(description = "exchange")
    private Boolean exchange;

    @Schema(description = "exchange remark")
    private String exchangeRemark;
}
