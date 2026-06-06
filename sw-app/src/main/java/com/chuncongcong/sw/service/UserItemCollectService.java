package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.bean.vo.ExchangeItemVO;
import com.chuncongcong.sw.bean.vo.ExchangeUserCardVO;
import com.chuncongcong.sw.bean.vo.ItemTopVO;
import com.chuncongcong.sw.bean.vo.TopVO;
import com.chuncongcong.sw.entity.UserItemCollectDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserItemCollectService extends IService<UserItemCollectDO> {

    List<UserItemCollectDO> listByUserIdAndItemIdList(Long userId, List<Long> itemIdList);

    List<UserItemCollectDO> listByUserId(Long userId);

    List<TopVO> maxTop(Integer itemSize);

    List<TopVO> minTop(Integer itemSize);

    List<ItemTopVO> minHold();

    List<ItemTopVO> maxReprint();

    List<ExchangeUserCardVO> listExchangeUsers(String keyword, Integer offset, Integer pageSize, Boolean random);

    List<ExchangeItemVO> listExchangeItemsByUserIds(List<Long> userIds, String keyword);

    List<ExchangeItemVO> listExchangeItemsByUserId(Long userId, Integer offset, Integer pageSize);

    List<ExchangeItemVO> listMyExchangeItems(Long userId);

}
