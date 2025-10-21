package com.chuncongcong.sw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chuncongcong.framework.bean.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("sw_item")
@EqualsAndHashCode(callSuper = true)
public class ItemDO extends BaseDO {

    /**
     * 地区ID
     */
    private Long regionId;

    /**
     * 名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 图片
     */
    private String img;

    /**
     * 排序
     */
    private Integer sort;

}
