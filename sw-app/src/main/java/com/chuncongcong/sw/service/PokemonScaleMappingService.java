package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.entity.PokemonScaleMappingDO;

import java.util.List;

public interface PokemonScaleMappingService extends IService<PokemonScaleMappingDO> {

    List<PokemonScaleMappingDO> listConfirmedByPokemonId(Long pokemonId);

    List<PokemonScaleMappingDO> listBySource(String sourceType, List<Long> sourceIds);
}
