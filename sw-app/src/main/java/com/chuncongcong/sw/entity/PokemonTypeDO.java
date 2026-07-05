package com.chuncongcong.sw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chuncongcong.framework.bean.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("sw_pokemon_type")
@EqualsAndHashCode(callSuper = true)
public class PokemonTypeDO extends BaseDO {

    private String code;

    private String nameZh;

    private String nameEn;

    private String iconUrl;

    private String localIcon;

    private Integer sort;
}
