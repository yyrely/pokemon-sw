package com.chuncongcong.sw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.entity.ItemInfoDO;
import com.chuncongcong.sw.mapper.ItemInfoMapper;
import com.chuncongcong.sw.service.ItemInfoService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ItemInfoServiceImpl extends ServiceImpl<ItemInfoMapper, ItemInfoDO> implements ItemInfoService {


    @Override
    public List<ItemInfoDO> listByItemId(Long itemId) {
        return this.lambdaQuery().eq(ItemInfoDO::getItemId, itemId).orderByDesc(ItemInfoDO::getDate).list();
    }

    @Override
    public LocalDate getLatestDate() {
        ItemInfoDO latest = this.lambdaQuery()
                .select(ItemInfoDO::getDate)
                .isNotNull(ItemInfoDO::getDate)
                .orderByDesc(ItemInfoDO::getDate)
                .last("LIMIT 1")
                .one();
        return latest == null ? null : latest.getDate();
    }

    @Override
    public List<Integer> listAvailableYears() {
        return this.lambdaQuery()
                .select(ItemInfoDO::getDate)
                .isNotNull(ItemInfoDO::getDate)
                .orderByDesc(ItemInfoDO::getDate)
                .list()
                .stream()
                .map(ItemInfoDO::getDate)
                .map(LocalDate::getYear)
                .distinct()
                .toList();
    }

    @Override
    public List<ItemInfoDO> listByYear(Integer year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        return this.lambdaQuery()
                .ge(ItemInfoDO::getDate, start)
                .lt(ItemInfoDO::getDate, start.plusYears(1))
                .orderByAsc(ItemInfoDO::getDate)
                .orderByAsc(ItemInfoDO::getType)
                .list();
    }
}
