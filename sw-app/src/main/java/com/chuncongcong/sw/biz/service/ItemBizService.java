package com.chuncongcong.sw.biz.service;

import com.chuncongcong.sw.bean.param.ExchangeSearchParam;
import com.chuncongcong.sw.bean.param.ItemCalendarParam;
import com.chuncongcong.sw.bean.param.ItemParam;
import com.chuncongcong.sw.bean.param.UserItemParam;
import com.chuncongcong.sw.bean.vo.ExchangeItemVO;
import com.chuncongcong.sw.bean.vo.ExchangeUserCardVO;
import com.chuncongcong.sw.bean.vo.ItemCalendarVO;
import com.chuncongcong.sw.bean.vo.ItemInfoVO;
import com.chuncongcong.sw.bean.vo.ItemTopVO;
import com.chuncongcong.sw.bean.vo.ItemVO;
import com.chuncongcong.sw.bean.vo.ReginItemCountVO;
import com.chuncongcong.sw.bean.vo.TopVO;
import com.chuncongcong.sw.bean.vo.UserItemVO;
import com.chuncongcong.sw.entity.ItemDO;
import com.chuncongcong.sw.entity.UserItemCollectDO;

import java.util.List;
import java.util.Map;

public interface ItemBizService {

    List<ItemDO> list(ItemParam param);

    ItemDO info(ItemParam param);

    ItemVO infoHasCollect(ItemParam param);

    List<ItemVO> listUserFirst(ItemParam param);

    List<ItemVO> listWithSubItems(ItemParam param);

    List<ItemVO> listItemsWithCollectStatus(Boolean collectStatus);

    List<UserItemVO> userList(ItemParam param);

    Map<Long, ReginItemCountVO> userCountByRegions(List<Long> regionIdList);

    void userSaveOrUpdate(UserItemParam param);

    void userDelete(UserItemParam param);

    void userExchange(UserItemParam param);

    List<ExchangeUserCardVO> exchangeCards(ExchangeSearchParam param);

    List<ExchangeItemVO> exchangeUserItems(ExchangeSearchParam param);

    List<ExchangeItemVO> myExchangeItems();

    List<ItemInfoVO> infoList(ItemParam param);

    ItemCalendarVO calendar(ItemCalendarParam param);

    void collectAll();

    List<TopVO> maxTop();

    List<TopVO> minTop();

    List<ItemTopVO> minHold();

    List<ItemTopVO> maxReprint();

}
