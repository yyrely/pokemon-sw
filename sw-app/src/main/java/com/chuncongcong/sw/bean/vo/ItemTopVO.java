package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "物品排行信息")
public class ItemTopVO {

    private Long itemId;

    private String itemName;

    private String itemImg;

    private Integer holdNumber;

    private BigDecimal holdRate;

    private LocalDate reprintDate;

    private String reprintDateMonth;

    private String reprintMonth;
}
