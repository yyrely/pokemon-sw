package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "预定转收藏参数")
public class ReservationConvertParam {

    @Schema(description = "预定记录ID")
    private Long id;

    @Schema(description = "收藏价格")
    private BigDecimal collectPrice;
}
