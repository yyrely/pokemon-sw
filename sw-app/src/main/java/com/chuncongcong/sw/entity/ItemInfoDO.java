package com.chuncongcong.sw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chuncongcong.framework.bean.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@TableName("sw_item_info")
@EqualsAndHashCode(callSuper = true)
public class ItemInfoDO extends BaseDO {

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 时间
     */
    private LocalDate date;

    /**
     * 类型，1-发售，2-再版，3-定番
     */
    private Integer type;

    /**
     * 价格
     */
    private String price;

}
