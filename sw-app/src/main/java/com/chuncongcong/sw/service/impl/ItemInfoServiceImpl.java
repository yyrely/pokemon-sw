package com.chuncongcong.sw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.entity.ItemDO;
import com.chuncongcong.sw.entity.ItemInfoDO;
import com.chuncongcong.sw.mapper.ItemInfoMapper;
import com.chuncongcong.sw.mapper.ItemMapper;
import com.chuncongcong.sw.service.ItemInfoService;
import com.chuncongcong.sw.service.ItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ItemInfoServiceImpl extends ServiceImpl<ItemInfoMapper, ItemInfoDO> implements ItemInfoService {


    @Override
    public List<ItemInfoDO> listByItemId(Long itemId) {
        return this.lambdaQuery().eq(ItemInfoDO::getItemId, itemId).orderByDesc(ItemInfoDO::getDate).list();
    }
}
