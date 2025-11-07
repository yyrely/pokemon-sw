package com.chuncongcong.sw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.bean.vo.TopVO;
import com.chuncongcong.sw.entity.UserItemCollectDO;
import com.chuncongcong.sw.mapper.UserItemCollectMapper;
import com.chuncongcong.sw.service.UserItemCollectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserItemCollectServiceImpl extends ServiceImpl<UserItemCollectMapper, UserItemCollectDO> implements UserItemCollectService {


    @Override
    public List<UserItemCollectDO> listByUserIdAndItemIdList(Long userId, List<Long> itemIdList) {
        return this.lambdaQuery().eq(UserItemCollectDO::getUserId, userId).in(UserItemCollectDO::getItemId, itemIdList)
                .orderByDesc(UserItemCollectDO::getCollectDate, UserItemCollectDO::getCreateTime).list();
    }

    @Override
    public List<UserItemCollectDO> listByUserId(Long userId) {
        return this.lambdaQuery().eq(UserItemCollectDO::getUserId, userId).list();
    }

    @Override
    public List<TopVO> maxTop(int itemSize) {
        return this.baseMapper.maxTop(itemSize);
    }

    @Override
    public List<TopVO> minTop(int itemSize) {
        return this.baseMapper.minTop(itemSize);
    }
}
