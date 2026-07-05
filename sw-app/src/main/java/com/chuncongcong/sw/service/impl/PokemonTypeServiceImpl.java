package com.chuncongcong.sw.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.entity.PokemonTypeDO;
import com.chuncongcong.sw.mapper.PokemonTypeMapper;
import com.chuncongcong.sw.service.PokemonTypeService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class PokemonTypeServiceImpl extends ServiceImpl<PokemonTypeMapper, PokemonTypeDO> implements PokemonTypeService {

    @Override
    public List<PokemonTypeDO> listAll() {
        return this.lambdaQuery()
                .orderByAsc(PokemonTypeDO::getSort)
                .list();
    }

    @Override
    public List<PokemonTypeDO> listByCodes(Collection<String> codes) {
        if (CollUtil.isEmpty(codes)) {
            return List.of();
        }
        return this.lambdaQuery()
                .in(PokemonTypeDO::getCode, codes)
                .orderByAsc(PokemonTypeDO::getSort)
                .list();
    }
}
