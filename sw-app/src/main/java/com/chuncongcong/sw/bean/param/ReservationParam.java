package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "预定保存参数")
public class ReservationParam {

    @Schema(description = "预定记录ID，新增时不传")
    private Long id;

    @Schema(description = "项目类型：1主项目，2子项目")
    private Integer itemType;

    @Schema(description = "主项目ID")
    private Long itemId;

    @Schema(description = "子项目ID列表，项目类型为2时必填")
    private List<Long> subItemIdList;

    @Schema(description = "预定类型：1全款，2定金")
    private Integer reservationType;

    @Schema(description = "预定金额：全款时为全款金额，定金时为定金金额")
    private BigDecimal reservationAmount;

    @Schema(description = "尾款金额，可为空")
    private BigDecimal balanceAmount;

    @Schema(description = "付尾款时间，可为空")
    private LocalDate balanceDueDate;

    @Schema(description = "到货时间")
    private LocalDate arrivalDate;
}
