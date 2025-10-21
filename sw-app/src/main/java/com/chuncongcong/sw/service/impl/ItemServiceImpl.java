package com.chuncongcong.sw.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.entity.ItemDO;
import com.chuncongcong.sw.mapper.ItemMapper;
import com.chuncongcong.sw.service.ItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, ItemDO> implements ItemService {

    @Override
    public List<ItemDO> listByRegionId(Long regionId) {
        return this.lambdaQuery().eq(Objects.nonNull(regionId), ItemDO::getRegionId, regionId)
                .orderByAsc(ItemDO::getRegionId)
                .orderByAsc(ItemDO::getSort).list();
    }

    @Override
    public List<ItemDO> listByRegionIds(List<Long> regionIdList) {
        return this.lambdaQuery().in(CollUtil.isNotEmpty(regionIdList), ItemDO::getRegionId, regionIdList).orderByAsc(ItemDO::getSort).list();
    }
}
