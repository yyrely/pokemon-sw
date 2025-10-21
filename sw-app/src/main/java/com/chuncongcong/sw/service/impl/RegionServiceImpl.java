package com.chuncongcong.sw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.entity.RegionDO;
import com.chuncongcong.sw.mapper.RegionMapper;
import com.chuncongcong.sw.service.RegionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, RegionDO> implements RegionService {

    @Override
    public List<RegionDO> listByGeneration(String generation) {
        return this.lambdaQuery()
                .eq(RegionDO::getGeneration, generation)
                .orderByAsc(RegionDO::getSort)
                .list();
    }

    @Override
    public RegionDO getByCode(String code) {
        return this.lambdaQuery()
                .eq(RegionDO::getCode, code)
                .one();
    }

    @Override
    public List<RegionDO> listOrderBySort() {
        return this.lambdaQuery().orderByAsc(RegionDO::getSort).list();
    }
}
