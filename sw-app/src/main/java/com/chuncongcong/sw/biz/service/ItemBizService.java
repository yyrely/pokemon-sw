package com.chuncongcong.sw.biz.service;

import com.chuncongcong.sw.bean.param.ItemParam;
import com.chuncongcong.sw.bean.param.UserItemParam;
import com.chuncongcong.sw.bean.vo.ItemInfoVO;
import com.chuncongcong.sw.bean.vo.ItemVO;
import com.chuncongcong.sw.bean.vo.ReginItemCountVO;
import com.chuncongcong.sw.bean.vo.TopVO;
import com.chuncongcong.sw.entity.ItemDO;
import com.chuncongcong.sw.entity.UserItemCollectDO;

import java.util.List;
import java.util.Map;

public interface ItemBizService {

    List<ItemDO> list(ItemParam param);

    ItemDO info(ItemParam param);

    List<ItemVO> listUserFirst(ItemParam param);

    List<ItemVO> listItemsWithCollectStatus(Boolean collectStatus);

    List<UserItemCollectDO> userList(ItemParam param);

    Map<Long, ReginItemCountVO> userCountByRegions(List<Long> regionIdList);

    void userSaveOrUpdate(UserItemParam param);

    void userDelete(UserItemParam param);

    List<ItemInfoVO> infoList(ItemParam param);

    List<TopVO> maxTop();

    List<TopVO> minTop();

}
