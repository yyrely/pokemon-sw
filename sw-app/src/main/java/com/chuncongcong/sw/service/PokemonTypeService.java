package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.entity.PokemonTypeDO;

import java.util.Collection;
import java.util.List;

public interface PokemonTypeService extends IService<PokemonTypeDO> {

    List<PokemonTypeDO> listAll();

    List<PokemonTypeDO> listByCodes(Collection<String> codes);
}
