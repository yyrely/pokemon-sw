package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "预定信息")
public class ReservationVO {

    private Long id;
    private Integer itemType;
    private Long itemId;
    private List<Long> subItemIdList;
    private String itemName;
    private String itemImg;
    private Integer reservationType;
    private BigDecimal reservationAmount;
    private BigDecimal balanceAmount;
    private LocalDate balanceDueDate;
    private LocalDate arrivalDate;

    @Schema(description = "到货月份，格式yyyy-MM")
    private String arrivalMonth;
}
