package com.chuncongcong.sw.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chuncongcong.framework.bean.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("sw_user_sub_item_collect")
@EqualsAndHashCode(callSuper = true)
public class UserSubItemCollectDO extends BaseDO {

    private Long userId;

    private Long itemId;

    private String subItemId;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private LocalDate collectDate;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private BigDecimal collectPrice;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String remark;

    @TableField("exchange_status")
    private Boolean exchange;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String exchangeRemark;
}
