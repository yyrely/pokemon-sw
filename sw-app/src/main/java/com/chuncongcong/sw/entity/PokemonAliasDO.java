package com.chuncongcong.sw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chuncongcong.framework.bean.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("sw_pokemon_alias")
@EqualsAndHashCode(callSuper = true)
public class PokemonAliasDO extends BaseDO {

    private Long pokemonId;

    private String alias;

    private String aliasType;
}
