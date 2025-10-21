package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.entity.ItemDO;

import java.util.List;

public interface ItemService extends IService<ItemDO> {

    List<ItemDO> listByRegionId(Long regionId);

    List<ItemDO> listByRegionIds(List<Long> regionIdList);

}
