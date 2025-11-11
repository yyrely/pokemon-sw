package com.chuncongcong.sw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.entity.UserItemSummaryDO;
import com.chuncongcong.sw.mapper.UserItemSummaryMapper;
import com.chuncongcong.sw.service.UserItemSummaryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserItemSummaryServiceImpl extends ServiceImpl<UserItemSummaryMapper, UserItemSummaryDO> implements UserItemSummaryService {

    @Override
    public List<UserItemSummaryDO> listByUserIdAndItemIds(Long userId, List<Long> itemIds) {
        return this.lambdaQuery().eq(UserItemSummaryDO::getUserId, userId)
                .in(UserItemSummaryDO::getItemId, itemIds).list();
    }

    @Override
    public UserItemSummaryDO getByUserIdAndItemId(Long userId, Long itemId) {
        return this.lambdaQuery().eq(UserItemSummaryDO::getUserId, userId)
                .eq(UserItemSummaryDO::getItemId, itemId).one();
    }

    @Override
    public List<UserItemSummaryDO> listByUserId(Long userId) {
        return this.lambdaQuery().eq(UserItemSummaryDO::getUserId, userId).list();
    }
}
