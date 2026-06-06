package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "user item")
public class UserItemParam {

    @Schema(description = "collect record id")
    private Long id;

    @Schema(description = "user id")
    private Long userId;

    @Schema(description = "item type: 1 main item, 2 sub item")
    private Integer itemType;

    @Schema(description = "item id")
    private Long itemId;

    @Schema(description = "sub item id")
    private Long subItemId;

    @Schema(description = "sub item id list")
    private List<Long> subItemIdList;

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
