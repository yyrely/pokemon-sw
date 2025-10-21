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
@TableName("sw_user_item")
@EqualsAndHashCode(callSuper = true)
public class UserItemDO extends BaseDO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 收集时间
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private LocalDate collectDate;

    /**
     * 收集价格
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private BigDecimal collectPrice;

    /**
     * 备注
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String remark;

}
