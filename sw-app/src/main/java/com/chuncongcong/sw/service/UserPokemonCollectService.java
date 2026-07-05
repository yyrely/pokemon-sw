package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.bean.vo.ExchangeItemVO;
import com.chuncongcong.sw.bean.vo.ExchangeUserCardVO;
import com.chuncongcong.sw.entity.UserPokemonCollectDO;

import java.util.List;

public interface UserPokemonCollectService extends IService<UserPokemonCollectDO> {

    List<UserPokemonCollectDO> listByUserIdAndPokemonId(Long userId, Long pokemonId);

    List<UserPokemonCollectDO> listByUserIdAndPokemonIds(Long userId, List<Long> pokemonIds);

    boolean removeScaleWorldSourcesByUserId(Long userId);

    boolean removeScaleWorldSource(Long userId, String sourceType, Long sourceCollectId);

    List<ExchangeUserCardVO> listExchangeUsers(String keyword, Integer offset, Integer pageSize, Boolean random);

    List<ExchangeItemVO> listExchangeItemsByUserIds(List<Long> userIds, String keyword);

    List<ExchangeItemVO> listExchangeItemsByUserId(Long userId, Integer offset, Integer pageSize);

    List<ExchangeItemVO> listMyExchangeItems(Long userId);
}
