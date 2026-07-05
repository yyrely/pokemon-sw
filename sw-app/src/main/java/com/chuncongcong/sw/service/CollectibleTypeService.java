package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.entity.CollectibleTypeDO;

import java.util.List;

public interface CollectibleTypeService extends IService<CollectibleTypeDO> {

    List<CollectibleTypeDO> listEnabled();
}
