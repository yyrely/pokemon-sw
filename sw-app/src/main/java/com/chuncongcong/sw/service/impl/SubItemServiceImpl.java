package com.chuncongcong.sw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.entity.SubItemDO;
import com.chuncongcong.sw.mapper.SubItemMapper;
import com.chuncongcong.sw.service.SubItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubItemServiceImpl extends ServiceImpl<SubItemMapper, SubItemDO> implements SubItemService {

    @Override
    public List<SubItemDO> listByItemId(Long itemId) {
        return this.lambdaQuery().eq(SubItemDO::getItemId, itemId).list();
    }

    @Override
    public List<SubItemDO> listAll() {
        return this.lambdaQuery()
                .orderByAsc(SubItemDO::getRegionId)
                .orderByAsc(SubItemDO::getItemId)
                .orderByAsc(SubItemDO::getSort)
                .list();
    }
}
