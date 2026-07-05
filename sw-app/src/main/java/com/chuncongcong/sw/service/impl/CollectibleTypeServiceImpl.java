package com.chuncongcong.sw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.entity.CollectibleTypeDO;
import com.chuncongcong.sw.mapper.CollectibleTypeMapper;
import com.chuncongcong.sw.service.CollectibleTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectibleTypeServiceImpl extends ServiceImpl<CollectibleTypeMapper, CollectibleTypeDO> implements CollectibleTypeService {

    @Override
    public List<CollectibleTypeDO> listEnabled() {
        return this.lambdaQuery()
                .eq(CollectibleTypeDO::getEnabled, true)
                .orderByAsc(CollectibleTypeDO::getSort)
                .list();
    }
}
