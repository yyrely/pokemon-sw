package com.chuncongcong.sw.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.bean.vo.ExchangeItemVO;
import com.chuncongcong.sw.bean.vo.ExchangeUserCardVO;
import com.chuncongcong.sw.entity.UserPokemonCollectDO;
import com.chuncongcong.sw.mapper.UserPokemonCollectMapper;
import com.chuncongcong.sw.service.UserPokemonCollectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPokemonCollectServiceImpl extends ServiceImpl<UserPokemonCollectMapper, UserPokemonCollectDO> implements UserPokemonCollectService {

    private static final String SOURCE_SCALE_ITEM = "scale_item";

    private static final String SOURCE_SCALE_SUB_ITEM = "scale_sub_item";

    @Override
    public List<UserPokemonCollectDO> listByUserIdAndPokemonId(Long userId, Long pokemonId) {
        return this.lambdaQuery()
                .eq(UserPokemonCollectDO::getUserId, userId)
                .eq(UserPokemonCollectDO::getPokemonId, pokemonId)
                .orderByDesc(UserPokemonCollectDO::getCollectDate, UserPokemonCollectDO::getCreateTime)
                .list();
    }

    @Override
    public List<UserPokemonCollectDO> listByUserIdAndPokemonIds(Long userId, List<Long> pokemonIds) {
        if (CollUtil.isEmpty(pokemonIds)) {
            return List.of();
        }
        return this.lambdaQuery()
                .eq(UserPokemonCollectDO::getUserId, userId)
                .in(UserPokemonCollectDO::getPokemonId, pokemonIds)
                .orderByDesc(UserPokemonCollectDO::getCollectDate, UserPokemonCollectDO::getCreateTime)
                .list();
    }

    @Override
    public boolean removeScaleWorldSourcesByUserId(Long userId) {
        return this.lambdaUpdate()
                .eq(UserPokemonCollectDO::getUserId, userId)
                .in(UserPokemonCollectDO::getSourceType, List.of(SOURCE_SCALE_ITEM, SOURCE_SCALE_SUB_ITEM))
                .remove();
    }

    @Override
    public boolean removeScaleWorldSource(Long userId, String sourceType, Long sourceCollectId) {
        return this.lambdaUpdate()
                .eq(UserPokemonCollectDO::getUserId, userId)
                .eq(UserPokemonCollectDO::getSourceType, sourceType)
                .eq(UserPokemonCollectDO::getSourceCollectId, sourceCollectId)
                .remove();
    }

    @Override
    public List<ExchangeUserCardVO> listExchangeUsers(String keyword, Integer offset, Integer pageSize, Boolean random) {
        return this.baseMapper.listExchangeUsers(keyword, offset, pageSize, random);
    }

    @Override
    public List<ExchangeItemVO> listExchangeItemsByUserIds(List<Long> userIds, String keyword) {
        if (CollUtil.isEmpty(userIds)) {
            return List.of();
        }
        return this.baseMapper.listExchangeItemsByUserIds(userIds, keyword);
    }

    @Override
    public List<ExchangeItemVO> listExchangeItemsByUserId(Long userId, Integer offset, Integer pageSize) {
        return this.baseMapper.listExchangeItemsByUserId(userId, offset, pageSize);
    }

    @Override
    public List<ExchangeItemVO> listMyExchangeItems(Long userId) {
        return this.baseMapper.listMyExchangeItems(userId);
    }
}
