package com.chuncongcong.sw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chuncongcong.framework.bean.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("sw_pokemon")
@EqualsAndHashCode(callSuper = true)
public class PokemonDO extends BaseDO {

    private Integer nationalNo;

    private Long regionId;

    private Integer generationNo;

    private String nameZh;

    private String nameEn;

    private String apiSpeciesName;

    private String apiPokemonName;

    private String typeNames;

    private String spriteUrl;

    private String localImg;

    private Integer sort;
}
