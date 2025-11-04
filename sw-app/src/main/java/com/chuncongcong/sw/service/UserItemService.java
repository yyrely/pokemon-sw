package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.bean.vo.TopVO;
import com.chuncongcong.sw.entity.UserItemDO;

import java.util.List;

public interface UserItemService extends IService<UserItemDO> {

    List<UserItemDO> listByUserIdAndItemIdList(Long userId, List<Long> itemIdList);

    List<UserItemDO> listByUserId(Long userId);

    List<TopVO> maxTop(int itemSize);

    List<TopVO> minTop(int itemSize);
}
