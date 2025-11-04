package com.chuncongcong.sw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.bean.vo.TopVO;
import com.chuncongcong.sw.entity.UserItemDO;
import com.chuncongcong.sw.mapper.UserItemMapper;
import com.chuncongcong.sw.service.UserItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserItemServiceImpl extends ServiceImpl<UserItemMapper, UserItemDO> implements UserItemService {


    @Override
    public List<UserItemDO> listByUserIdAndItemIdList(Long userId, List<Long> itemIdList) {
        return this.lambdaQuery().eq(UserItemDO::getUserId, userId).in(UserItemDO::getItemId, itemIdList)
                .orderByDesc(UserItemDO::getCollectDate, UserItemDO::getCreateTime).list();
    }

    @Override
    public List<UserItemDO> listByUserId(Long userId) {
        return this.lambdaQuery().eq(UserItemDO::getUserId, userId).list();
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
