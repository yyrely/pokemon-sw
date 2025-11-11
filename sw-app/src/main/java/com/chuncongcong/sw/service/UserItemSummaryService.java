package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.entity.UserItemSummaryDO;

import java.util.List;

public interface UserItemSummaryService extends IService<UserItemSummaryDO> {

    List<UserItemSummaryDO> listByUserIdAndItemIds(Long userId, List<Long> itemIds);

    UserItemSummaryDO getByUserIdAndItemId(Long userId, Long itemId);

    List<UserItemSummaryDO> listByUserId(Long userId);
}
