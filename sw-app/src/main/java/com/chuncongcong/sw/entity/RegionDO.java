package com.chuncongcong.sw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chuncongcong.framework.bean.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("sw_region")
@EqualsAndHashCode(callSuper = true)
public class RegionDO extends BaseDO {

    /**
     * 名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 世代
     */
    private String generation;

    /**
     * 图片
     */
    private String img;

    /**
     * 排序
     */
    private Integer sort;

}
