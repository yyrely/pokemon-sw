package com.chuncongcong.sw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.entity.UserItemCollectDO;
import com.chuncongcong.sw.entity.UserSubItemCollectDO;
import com.chuncongcong.sw.mapper.UserSubItemCollectMapper;
import com.chuncongcong.sw.service.UserSubItemCollectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSubItemCollectServiceImpl extends ServiceImpl<UserSubItemCollectMapper, UserSubItemCollectDO> implements UserSubItemCollectService {

    @Override
    public List<UserSubItemCollectDO> listByUserIdAndItemId(Long userId, Long itemId) {
        return this.lambdaQuery().eq(UserSubItemCollectDO::getUserId, userId).eq(UserSubItemCollectDO::getItemId, itemId)
                .orderByDesc(UserSubItemCollectDO::getCollectDate, UserSubItemCollectDO::getCreateTime).list();
    }

    @Override
    public List<UserSubItemCollectDO> listByUserId(Long userId) {
        return this.lambdaQuery().eq(UserSubItemCollectDO::getUserId, userId).list();

    }

    @Override
    public List<UserSubItemCollectDO> listByUserIdAndItemIdList(Long userId, List<Long> itemIds) {
        return this.lambdaQuery().eq(UserSubItemCollectDO::getUserId, userId).in(UserSubItemCollectDO::getItemId, itemIds)
                .orderByDesc(UserSubItemCollectDO::getCollectDate, UserSubItemCollectDO::getCreateTime).list();
    }
}
