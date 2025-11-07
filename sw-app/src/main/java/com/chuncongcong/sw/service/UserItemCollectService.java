package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.bean.vo.TopVO;
import com.chuncongcong.sw.entity.UserItemCollectDO;

import java.util.List;

public interface UserItemCollectService extends IService<UserItemCollectDO> {

    List<UserItemCollectDO> listByUserIdAndItemIdList(Long userId, List<Long> itemIdList);

    List<UserItemCollectDO> listByUserId(Long userId);

    List<TopVO> maxTop(int itemSize);

    List<TopVO> minTop(int itemSize);
}
