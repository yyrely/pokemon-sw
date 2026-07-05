package com.chuncongcong.sw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chuncongcong.framework.bean.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("sw_collectible_type")
@EqualsAndHashCode(callSuper = true)
public class CollectibleTypeDO extends BaseDO {

    private String code;

    private String name;

    private Integer sort;

    private Boolean enabled;
}
