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
import com.chuncongcong.sw.entity.UserItemCollectDO;
import com.chuncongcong.sw.service.ItemInfoService;
import com.chuncongcong.sw.service.ItemService;
import com.chuncongcong.sw.service.UserItemCollectService;
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
    private UserItemCollectService userItemCollectService;
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
        List<UserItemCollectDO> userItemCollectDOS = userItemCollectService.listByUserIdAndItemIdList(ContextHolder.getUserId(), itemIds);
        if (userItemCollectDOS.isEmpty()) {
            return itemVOS;
        }
        Map<Long, UserItemCollectDO> UserItemCollectDOMap = getUserItemMinPriceMap(userItemCollectDOS);
        List<ItemVO> matched = new ArrayList<>();
        List<ItemVO> unmatched = new ArrayList<>();

        for (ItemVO vo : itemVOS) {
            UserItemCollectDO UserItemCollectDO = UserItemCollectDOMap.get(vo.getId());
            if (Objects.nonNull(UserItemCollectDO)) {
                vo.setCollect(true);
                vo.setMinCollectPrice(UserItemCollectDO.getCollectPrice());
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
        List<UserItemCollectDO> userItemCollectDOList = userItemCollectService.listByUserId(ContextHolder.getUserId());
        Map<Long, UserItemCollectDO> userItemCollectDOMap = new HashMap<>();
        if (CollUtil.isNotEmpty(userItemCollectDOList)) {
            userItemCollectDOMap = getUserItemMinPriceMap(userItemCollectDOList);
        }

        List<ItemDO> itemDOS = itemService.listByRegionId(null);
        List<ItemVO> itemVOS = new ArrayList<>();
        for (ItemDO itemDO : itemDOS) {
            ItemVO vo = BeanUtil.toBean(itemDO, ItemVO.class);
            UserItemCollectDO userItemCollectDO = userItemCollectDOMap.get(itemDO.getId());
            if (Boolean.TRUE.equals(collectStatus) && Objects.nonNull(userItemCollectDO)) {
                vo.setCollect(true);
                vo.setMinCollectPrice(userItemCollectDO.getCollectPrice());
                itemVOS.add(vo);
            }
            if (Boolean.FALSE.equals(collectStatus) && Objects.isNull(userItemCollectDO)) {
                vo.setCollect(false);
                itemVOS.add(vo);
            }
        }
        return itemVOS;
    }

    private Map<Long, UserItemCollectDO> getUserItemMinPriceMap(List<UserItemCollectDO> userItemCollectDOList) {
        return userItemCollectDOList.stream()
                .collect(Collectors.toMap(
                        UserItemCollectDO::getItemId,
                        Function.identity(),
                        BinaryOperator.minBy(Comparator.comparing(
                                UserItemCollectDO::getCollectPrice,
                                Comparator.nullsLast(BigDecimal::compareTo)
                        ))
                ));
    }

    @Override
    public List<UserItemCollectDO> userList(ItemParam param) {
        if (Objects.isNull(param.getId())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        return userItemCollectService.listByUserIdAndItemIdList(ContextHolder.getUserId(), Collections.singletonList(param.getId()));
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
        List<UserItemCollectDO> userItemList = userItemCollectService.listByUserIdAndItemIdList(userId, allItemIds);
        Map<Long, List<UserItemCollectDO>> userItemMap = userItemList.stream().collect(Collectors.groupingBy(UserItemCollectDO::getItemId));

        // 针对每个 region 计算
        Map<Long, ReginItemCountVO> result = new HashMap<>();
        for (Long regionId : regionIdList) {
            List<ItemDO> regionItems = itemByRegionMap.getOrDefault(regionId, Collections.emptyList());

            ReginItemCountVO vo = new ReginItemCountVO();
            vo.setRegionId(regionId);
            vo.setCount(regionItems.size());

            if (!regionItems.isEmpty()) {
                List<Long> regionItemIds = regionItems.stream().map(ItemDO::getId).toList();
                List<UserItemCollectDO> regionUserItems = regionItemIds.stream()
                        .flatMap(id -> userItemMap.getOrDefault(id, Collections.emptyList()).stream())
                        .toList();

                vo.setCollectCount((int) regionUserItems.stream().map(UserItemCollectDO::getItemId).distinct().count());
                vo.setUnCollectCount(vo.getCount() - vo.getCollectCount());
                vo.setCollectRate(BigDecimal.valueOf(vo.getCollectCount()).divide(BigDecimal.valueOf(vo.getCount()), 4, RoundingMode.HALF_UP));
                vo.setCollectNumber(regionUserItems.size());
                vo.setCollectPriceTotal(regionUserItems.stream()
                        .map(UserItemCollectDO::getCollectPrice)
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
        UserItemCollectDO userItemCollectDO = BeanUtil.copyProperties(param, UserItemCollectDO.class);
        userItemCollectDO.setUserId(ContextHolder.getUserId());
        userItemCollectService.saveOrUpdate(userItemCollectDO);
    }

    @Override
    public void userDelete(UserItemParam param) {
        if (Objects.isNull(param.getId())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        userItemCollectService.removeById(param.getId());
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
        List<TopVO> topVOS = userItemCollectService.maxTop(itemDOS.size());
        List<Long> userIds = topVOS.stream().map(TopVO::getUserId).toList();
        topFill(userIds, topVOS);
        return topVOS;
    }

    @Override
    public List<TopVO> minTop() {
        List<ItemDO> itemDOS = itemService.listByRegionId(null);
        List<TopVO> topVOS = userItemCollectService.minTop(itemDOS.size());
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








