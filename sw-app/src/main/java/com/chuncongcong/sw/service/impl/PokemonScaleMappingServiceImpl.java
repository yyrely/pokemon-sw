package com.chuncongcong.sw.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.entity.PokemonScaleMappingDO;
import com.chuncongcong.sw.mapper.PokemonScaleMappingMapper;
import com.chuncongcong.sw.service.PokemonScaleMappingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PokemonScaleMappingServiceImpl extends ServiceImpl<PokemonScaleMappingMapper, PokemonScaleMappingDO> implements PokemonScaleMappingService {

    @Override
    public List<PokemonScaleMappingDO> listConfirmedByPokemonId(Long pokemonId) {
        return this.lambdaQuery()
                .eq(PokemonScaleMappingDO::getPokemonId, pokemonId)
                .eq(PokemonScaleMappingDO::getConfirmed, true)
                .list();
    }

    @Override
    public List<PokemonScaleMappingDO> listBySource(String sourceType, List<Long> sourceIds) {
        if (CollUtil.isEmpty(sourceIds)) {
            return List.of();
        }
        return this.lambdaQuery()
                .eq(PokemonScaleMappingDO::getSourceType, sourceType)
                .in(PokemonScaleMappingDO::getSourceId, sourceIds)
                .list();
    }
}
