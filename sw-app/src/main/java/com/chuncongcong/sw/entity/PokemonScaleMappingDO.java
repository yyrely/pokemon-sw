package com.chuncongcong.sw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chuncongcong.framework.bean.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@TableName("sw_pokemon_scale_mapping")
@EqualsAndHashCode(callSuper = true)
public class PokemonScaleMappingDO extends BaseDO {

    private Long pokemonId;

    private String sourceType;

    private Long sourceId;

    private String matchType;

    private BigDecimal matchScore;

    private Boolean confirmed;
}
