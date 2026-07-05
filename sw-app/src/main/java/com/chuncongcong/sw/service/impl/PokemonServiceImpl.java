package com.chuncongcong.sw.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.entity.PokemonDO;
import com.chuncongcong.sw.mapper.PokemonMapper;
import com.chuncongcong.sw.service.PokemonService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PokemonServiceImpl extends ServiceImpl<PokemonMapper, PokemonDO> implements PokemonService {

    @Override
    public List<PokemonDO> listByRegionId(Long regionId, String keyword) {
        return this.lambdaQuery()
                .eq(Objects.nonNull(regionId), PokemonDO::getRegionId, regionId)
                .and(CharSequenceUtil.isNotBlank(keyword), wrapper -> wrapper
                        .like(PokemonDO::getNameZh, keyword)
                        .or()
                        .like(PokemonDO::getNameEn, keyword)
                        .or()
                        .eq(PokemonDO::getNationalNo, parseNumber(keyword)))
                .orderByAsc(PokemonDO::getRegionId)
                .orderByAsc(PokemonDO::getSort)
                .list();
    }

    private Integer parseNumber(String keyword) {
        try {
            return Integer.valueOf(keyword);
        } catch (Exception ignored) {
            return -1;
        }
    }
}
