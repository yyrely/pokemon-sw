package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.entity.SubItemDO;

import java.util.List;

public interface SubItemService extends IService<SubItemDO> {
    List<SubItemDO> listByItemId(Long itemId);
}
