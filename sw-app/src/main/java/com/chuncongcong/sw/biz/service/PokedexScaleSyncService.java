package com.chuncongcong.sw.biz.service;

import com.chuncongcong.sw.bean.vo.PokedexScaleSyncResultVO;
import com.chuncongcong.sw.entity.UserItemCollectDO;
import com.chuncongcong.sw.entity.UserSubItemCollectDO;

public interface PokedexScaleSyncService {

    PokedexScaleSyncResultVO syncUserScaleCollects(Long userId);

    void syncItemCollect(UserItemCollectDO record);

    void syncSubItemCollect(UserSubItemCollectDO record);

    void deleteItemCollectSource(Long userId, Long sourceCollectId);

    void deleteSubItemCollectSource(Long userId, Long sourceCollectId);
}
