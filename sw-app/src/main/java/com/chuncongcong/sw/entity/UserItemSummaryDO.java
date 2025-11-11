package com.chuncongcong.sw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chuncongcong.framework.bean.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("sw_user_item_summary")
@EqualsAndHashCode(callSuper = true)
public class UserItemSummaryDO extends BaseDO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 收集状态
     */
    private Boolean collectStatus;

    /**
     * 收集类型
     */
    private Integer collectType;

    /**
     * 子物品ID列表
     */
    private String subItemIdList;

}
