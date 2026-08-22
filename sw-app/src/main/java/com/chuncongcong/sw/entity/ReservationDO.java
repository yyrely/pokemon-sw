package com.chuncongcong.sw.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chuncongcong.framework.bean.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sw_reservation")
@EqualsAndHashCode(callSuper = true)
public class ReservationDO extends BaseDO {

    private Long userId;
    private Integer itemType;
    private Long itemId;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String subItemId;

    private Integer reservationType;
    private BigDecimal reservationAmount;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private BigDecimal balanceAmount;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private LocalDate balanceDueDate;

    private LocalDate arrivalDate;
    private Integer status;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private LocalDateTime convertedTime;
}
