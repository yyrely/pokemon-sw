package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.entity.UserSubItemCollectDO;

import java.util.List;

public interface UserSubItemCollectService extends IService<UserSubItemCollectDO> {


    List<UserSubItemCollectDO> listByUserIdAndItemId(Long userId, Long itemId);

    List<UserSubItemCollectDO> listByUserId(Long userId);

    List<UserSubItemCollectDO> listByUserIdAndItemIdList(Long userId, List<Long> itemIds);
}
