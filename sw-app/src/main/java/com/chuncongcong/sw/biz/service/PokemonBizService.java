package com.chuncongcong.sw.biz.service;

import com.chuncongcong.sw.bean.param.PokemonParam;
import com.chuncongcong.sw.bean.param.UserPokemonCollectParam;
import com.chuncongcong.sw.bean.vo.PokemonStatsVO;
import com.chuncongcong.sw.bean.vo.PokemonTypeVO;
import com.chuncongcong.sw.bean.vo.PokemonVO;
import com.chuncongcong.sw.bean.vo.UserPokemonCollectVO;
import com.chuncongcong.sw.entity.CollectibleTypeDO;

import java.util.List;

public interface PokemonBizService {

    List<PokemonVO> list(PokemonParam param);

    PokemonVO info(PokemonParam param);

    List<UserPokemonCollectVO> userCollectList(UserPokemonCollectParam param);

    void userSaveOrUpdate(UserPokemonCollectParam param);

    void userDelete(UserPokemonCollectParam param);

    void userExchange(UserPokemonCollectParam param);

    PokemonStatsVO stats(PokemonParam param);

    List<CollectibleTypeDO> collectibleTypes();

    List<PokemonTypeVO> pokemonTypes();
}
