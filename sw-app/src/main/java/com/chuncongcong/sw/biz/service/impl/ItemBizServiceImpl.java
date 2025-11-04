package com.chuncongcong.sw.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.chuncongcong.framework.exception.ServiceException;
import com.chuncongcong.framework.util.ContextHolder;
import com.chuncongcong.sw.bean.param.ItemParam;
import com.chuncongcong.sw.bean.param.UserItemParam;
import com.chuncongcong.sw.bean.vo.ItemInfoVO;
import com.chuncongcong.sw.bean.vo.ItemVO;
import com.chuncongcong.sw.bean.vo.ReginItemCountVO;
import com.chuncongcong.sw.bean.vo.TopVO;
import com.chuncongcong.sw.biz.service.ItemBizService;
import com.chuncongcong.sw.entity.ItemDO;
import com.chuncongcong.sw.entity.ItemInfoDO;
import com.chuncongcong.sw.entity.UserDO;
import com.chuncongcong.sw.entity.UserItemDO;
import com.chuncongcong.sw.service.ItemInfoService;
import com.chuncongcong.sw.service.ItemService;
import com.chuncongcong.sw.service.UserItemService;
import com.chuncongcong.sw.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.chuncongcong.framework.exception.ServiceErrorEnum.PARAM_ERROR;

@Service
public class ItemBizServiceImpl implements ItemBizService {

    @Resource
    private ItemService itemService;
    @Resource
    private UserItemService userItemService;
    @Resource
    private ItemInfoService itemInfoService;
    @Resource
    private UserService userService;

    @Override
    public List<ItemDO> list(ItemParam param) {
        return itemService.listByRegionId(param.getRegionId());
    }

    @Override
    public ItemDO info(ItemParam param) {
        if (Objects.isNull(param.getId())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        return itemService.getById(param.getId());
    }

    @Override
    public List<ItemVO> listUserFirst(ItemParam param) {
        List<ItemDO> itemDOList = list(param);
        if (itemDOList.isEmpty()) {
            return List.of();
        }

        List<ItemVO> itemVOS = BeanUtil.copyToList(itemDOList, ItemVO.class);
        List<Long> itemIds = itemVOS.stream().map(ItemVO::getId).toList();
        List<UserItemDO> userItemDOList = userItemService.listByUserIdAndItemIdList(ContextHolder.getUserId(), itemIds);
        if (userItemDOList.isEmpty()) {
            return itemVOS;
        }
        Map<Long, UserItemDO> userItemDOMap = getUserItemMinPriceMap(userItemDOList);
        List<ItemVO> matched = new ArrayList<>();
        List<ItemVO> unmatched = new ArrayList<>();

        for (ItemVO vo : itemVOS) {
            UserItemDO userItemDO = userItemDOMap.get(vo.getId());
            if (Objects.nonNull(userItemDO)) {
                vo.setCollect(true);
                vo.setMinCollectPrice(userItemDO.getCollectPrice());
                matched.add(vo);
            } else {
                unmatched.add(vo);
            }
        }

        List<ItemVO> orderedList = new ArrayList<>();
        orderedList.addAll(matched);
        orderedList.addAll(unmatched);
        return orderedList;
    }

    @Override
    public List<ItemVO> listItemsWithCollectStatus(Boolean collectStatus) {
        List<UserItemDO> userItemDOList = userItemService.listByUserId(ContextHolder.getUserId());
        Map<Long, UserItemDO> userItemDOMap = new HashMap<>();
        if (CollUtil.isNotEmpty(userItemDOList)) {
            userItemDOMap = getUserItemMinPriceMap(userItemDOList);
        }

        List<ItemDO> itemDOS = itemService.listByRegionId(null);
        List<ItemVO> itemVOS = new ArrayList<>();
        for (ItemDO itemDO : itemDOS) {
            ItemVO vo = BeanUtil.toBean(itemDO, ItemVO.class);
            UserItemDO userItemDO = userItemDOMap.get(itemDO.getId());
            if (Boolean.TRUE.equals(collectStatus) && Objects.nonNull(userItemDO)) {
                vo.setCollect(true);
                vo.setMinCollectPrice(userItemDO.getCollectPrice());
                itemVOS.add(vo);
            }
            if (Boolean.FALSE.equals(collectStatus) && Objects.isNull(userItemDO)) {
                vo.setCollect(false);
                itemVOS.add(vo);
            }
        }
        return itemVOS;
    }

    private Map<Long, UserItemDO> getUserItemMinPriceMap(List<UserItemDO> userItemDOList) {
        return userItemDOList.stream()
                .collect(Collectors.toMap(
                        UserItemDO::getItemId,
                        Function.identity(),
                        BinaryOperator.minBy(Comparator.comparing(
                                UserItemDO::getCollectPrice,
                                Comparator.nullsLast(BigDecimal::compareTo)
                        ))
                ));
    }

    @Override
    public List<UserItemDO> userList(ItemParam param) {
        if (Objects.isNull(param.getId())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        return userItemService.listByUserIdAndItemIdList(ContextHolder.getUserId(), Collections.singletonList(param.getId()));
    }

    @Override
    public Map<Long, ReginItemCountVO> userCountByRegions(List<Long> regionIdList) {
        List<ItemDO> allItemList = itemService.listByRegionIds(regionIdList);
        if (allItemList.isEmpty()) {
            return Map.of();
        }
        // 按 regionId 分组
        Map<Long, List<ItemDO>> itemByRegionMap = allItemList.stream()
                .collect(Collectors.groupingBy(ItemDO::getRegionId));

        // 用户收藏
        List<Long> allItemIds = allItemList.stream().map(ItemDO::getId).toList();
        Long userId = ContextHolder.getUserId();
        List<UserItemDO> userItemList = userItemService.listByUserIdAndItemIdList(userId, allItemIds);
        Map<Long, List<UserItemDO>> userItemMap = userItemList.stream().collect(Collectors.groupingBy(UserItemDO::getItemId));

        // 针对每个 region 计算
        Map<Long, ReginItemCountVO> result = new HashMap<>();
        for (Long regionId : regionIdList) {
            List<ItemDO> regionItems = itemByRegionMap.getOrDefault(regionId, Collections.emptyList());

            ReginItemCountVO vo = new ReginItemCountVO();
            vo.setRegionId(regionId);
            vo.setCount(regionItems.size());

            if (!regionItems.isEmpty()) {
                List<Long> regionItemIds = regionItems.stream().map(ItemDO::getId).toList();
                List<UserItemDO> regionUserItems = regionItemIds.stream()
                        .flatMap(id -> userItemMap.getOrDefault(id, Collections.emptyList()).stream())
                        .toList();

                vo.setCollectCount((int) regionUserItems.stream().map(UserItemDO::getItemId).distinct().count());
                vo.setUnCollectCount(vo.getCount() - vo.getCollectCount());
                vo.setCollectRate(BigDecimal.valueOf(vo.getCollectCount()).divide(BigDecimal.valueOf(vo.getCount()), 4, RoundingMode.HALF_UP));
                vo.setCollectNumber(regionUserItems.size());
                vo.setCollectPriceTotal(regionUserItems.stream()
                        .map(UserItemDO::getCollectPrice)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO));
            }
            result.put(regionId, vo);
        }
        return result;
    }


    @Override
    public void userSaveOrUpdate(UserItemParam param) {
        UserItemDO userItemDO = BeanUtil.copyProperties(param, UserItemDO.class);
        userItemDO.setUserId(ContextHolder.getUserId());
        userItemService.saveOrUpdate(userItemDO);
    }

    @Override
    public void userDelete(UserItemParam param) {
        if (Objects.isNull(param.getId())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        userItemService.removeById(param.getId());
    }

    @Override
    public List<ItemInfoVO> infoList(ItemParam param) {
        if (Objects.isNull(param.getId())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        List<ItemInfoDO> itemInfoDOList = itemInfoService.listByItemId(param.getId());
        return BeanUtil.copyToList(itemInfoDOList, ItemInfoVO.class);
    }

    @Override
    public List<TopVO> maxTop() {
        List<ItemDO> itemDOS = itemService.listByRegionId(null);
        List<TopVO> topVOS = userItemService.maxTop(itemDOS.size());
        List<Long> userIds = topVOS.stream().map(TopVO::getUserId).toList();
        topFill(userIds, topVOS);
        return topVOS;
    }

    @Override
    public List<TopVO> minTop() {
        List<ItemDO> itemDOS = itemService.listByRegionId(null);
        List<TopVO> topVOS = userItemService.minTop(itemDOS.size());
        List<Long> userIds = topVOS.stream().map(TopVO::getUserId).toList();
        topFill(userIds, topVOS);
        return topVOS;
    }

    private void topFill(List<Long> userIds, List<TopVO> topVOS) {
        List<UserDO> userDOS = userService.listByIds(userIds);
        Map<Long, UserDO> userDOMap = userDOS.stream().collect(Collectors.toMap(UserDO::getId, Function.identity()));
        topVOS.forEach(vo -> {
            UserDO userDO = userDOMap.get(vo.getUserId());
            vo.setUsername(userDO.getUsername());
            vo.setHeadImgUrl(userDO.getHeadImgUrl());
        });
    }
}








