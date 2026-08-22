package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.entity.ItemDO;
import com.chuncongcong.sw.entity.ItemInfoDO;

import java.time.LocalDate;
import java.util.List;

public interface ItemInfoService extends IService<ItemInfoDO> {

    List<ItemInfoDO> listByItemId(Long itemId);

    LocalDate getLatestDate();

    List<Integer> listAvailableYears();

    List<ItemInfoDO> listByYear(Integer year);

}
