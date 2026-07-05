package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.entity.PokemonDO;

import java.util.List;

public interface PokemonService extends IService<PokemonDO> {

    List<PokemonDO> listByRegionId(Long regionId, String keyword);
}
