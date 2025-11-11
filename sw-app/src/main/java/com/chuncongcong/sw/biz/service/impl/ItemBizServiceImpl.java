package com.chuncongcong.sw.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.chuncongcong.framework.exception.ServiceException;
import com.chuncongcong.framework.util.ContextHolder;
import com.chuncongcong.sw.bean.enums.ItemTypeEnum;
import com.chuncongcong.sw.bean.param.ItemParam;
import com.chuncongcong.sw.bean.param.UserItemParam;
import com.chuncongcong.sw.bean.vo.*;
import com.chuncongcong.sw.biz.service.ItemBizService;
import com.chuncongcong.sw.entity.*;
import com.chuncongcong.sw.service.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private SubItemService subItemService;
    @Resource
    private ItemInfoService itemInfoService;
    @Resource
    private UserItemCollectService userItemCollectService;
    @Resource
    private UserSubItemCollectService userSubItemCollectService;
    @Resource
    private UserItemSummaryService userItemSummaryService;
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
    public ItemVO infoHasCollect(ItemParam param) {
        ItemDO itemDO = info(param);
        ItemVO itemVO = BeanUtil.toBean(itemDO, ItemVO.class);
        List<SubItemDO> subItemDOS = subItemService.listByItemId(itemDO.getId());
        List<SubItemVO> subItemList = new ArrayList<>();
        for (SubItemDO subItemDO : subItemDOS) {
            SubItemVO subItemVO = BeanUtil.toBean(subItemDO, SubItemVO.class);
            subItemList.add(subItemVO);
        }
        itemVO.setSubItemList(subItemList);

        UserItemSummaryDO userItemSummaryDO = userItemSummaryService.getByUserIdAndItemId(ContextHolder.getUserId(), itemDO.getId());
        if (Objects.nonNull(userItemSummaryDO)) {
            Set<Long> subItemIdListSet = new HashSet<>();
            if (CharSequenceUtil.isNotEmpty(userItemSummaryDO.getSubItemIdList())) {
                subItemIdListSet = new HashSet<>(JSONUtil.parseArray(userItemSummaryDO.getSubItemIdList()).toList(Long.class));
            }
            for (SubItemVO subItemVO : subItemList) {
                if (Boolean.TRUE.equals(userItemSummaryDO.getCollectStatus()) && ItemTypeEnum.ITEM.getCode().equals(userItemSummaryDO.getCollectType())) {
                    subItemVO.setCollect(Boolean.TRUE);
                } else {
                    subItemVO.setCollect(subItemIdListSet.contains(subItemVO.getId()));
                }
            }
            itemVO.setCollect(userItemSummaryDO.getCollectStatus());
        }
        return itemVO;
    }

    @Override
    public List<ItemVO> listUserFirst(ItemParam param) {
        List<ItemDO> itemDOList = list(param);
        if (itemDOList.isEmpty()) {
            return List.of();
        }

        List<ItemVO> itemVOS = BeanUtil.copyToList(itemDOList, ItemVO.class);
        List<Long> itemIds = itemVOS.stream().map(ItemVO::getId).toList();

        List<UserItemSummaryDO> userItemSummaryDOS = userItemSummaryService.listByUserIdAndItemIds(ContextHolder.getUserId(), itemIds);
        if (userItemSummaryDOS.isEmpty()) {
            return itemVOS;
        }
        Map<Long, UserItemSummaryDO> userItemSummaryDOMap = userItemSummaryDOS.stream().collect(Collectors.toMap(UserItemSummaryDO::getItemId, Function.identity()));
        List<ItemVO> matched = new ArrayList<>();
        List<ItemVO> unmatched = new ArrayList<>();

        for (ItemVO vo : itemVOS) {
            UserItemSummaryDO userItemSummaryDO = userItemSummaryDOMap.get(vo.getId());
            if (Objects.nonNull(userItemSummaryDO) && Boolean.TRUE.equals(userItemSummaryDO.getCollectStatus())) {
                vo.setCollect(true);
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
        List<UserSubItemCollectDO> userSubItemCollectDOS = userSubItemCollectService.listByUserId(ContextHolder.getUserId());
        Map<Long, UserItemCollectDO> userItemCollectDOMap = new HashMap<>();
        Map<Long, UserSubItemCollectDO> userSubItemCollectDOMap = new HashMap<>();
        if (CollUtil.isNotEmpty(userItemCollectDOList)) {
            userItemCollectDOMap = getUserItemMinPriceMap(userItemCollectDOList);
        }
        if (CollUtil.isNotEmpty(userSubItemCollectDOS)) {
            userSubItemCollectDOMap = getUserSubItemMinPriceMap(userSubItemCollectDOS);
        }

        List<ItemDO> itemDOS = itemService.listByRegionId(null);
        List<SubItemDO> subItemDOS = subItemService.listAll();
        Map<Long, List<SubItemDO>> subItemGroupMap = subItemDOS.stream().collect(Collectors.groupingBy(SubItemDO::getItemId));

        List<ItemVO> itemVOS = new ArrayList<>();
        for (ItemDO itemDO : itemDOS) {

            UserItemCollectDO userItemCollectDO = userItemCollectDOMap.get(itemDO.getId());
            if (Boolean.TRUE.equals(collectStatus)) {
                if (Objects.nonNull(userItemCollectDO)) {
                    ItemVO vo = BeanUtil.toBean(itemDO, ItemVO.class);
                    vo.setCollect(true);
                    vo.setMinCollectPrice(userItemCollectDO.getCollectPrice());
                    itemVOS.add(vo);
                } else {
                    addSubItemCollect(subItemGroupMap, userSubItemCollectDOMap, itemDO.getId(), itemVOS);
                }
            }
            if (Boolean.FALSE.equals(collectStatus) && Objects.isNull(userItemCollectDO)) {
                ItemVO vo = BeanUtil.toBean(itemDO, ItemVO.class);
                vo.setCollect(false);
                itemVOS.add(vo);
            }
        }
        return itemVOS;
    }

    private void addSubItemCollect(Map<Long, List<SubItemDO>> subItemGroupMap, Map<Long, UserSubItemCollectDO> userSubItemCollectDOMap, Long itemId, List<ItemVO> itemVOS) {
        List<SubItemDO> itemSubItemDOS = subItemGroupMap.get(itemId);
        if (CollUtil.isEmpty(itemSubItemDOS)) {
            return;
        }
        for (SubItemDO subItemDO : itemSubItemDOS) {
            UserSubItemCollectDO userSubItemCollectDO = userSubItemCollectDOMap.get(subItemDO.getId());
            if (Objects.isNull(userSubItemCollectDO)) {
                continue;
            }
            ItemVO vo = BeanUtil.toBean(subItemDO, ItemVO.class);
            vo.setId(subItemDO.getItemId());
            vo.setCollect(true);
            vo.setMinCollectPrice(userSubItemCollectDO.getCollectPrice());
            itemVOS.add(vo);
        }
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

    private Map<Long, UserSubItemCollectDO> getUserSubItemMinPriceMap(List<UserSubItemCollectDO> userSubItemCollectDOList) {
        return userSubItemCollectDOList.stream()
                .collect(Collectors.toMap(
                        UserSubItemCollectDO::getSubItemId,
                        Function.identity(),
                        BinaryOperator.minBy(Comparator.comparing(
                                UserSubItemCollectDO::getCollectPrice,
                                Comparator.nullsLast(BigDecimal::compareTo)
                        ))
                ));
    }

    @Override
    public List<UserItemVO> userList(ItemParam param) {
        if (Objects.isNull(param.getId())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        ItemDO itemDO = itemService.getById(param.getId());
        List<SubItemDO> subItemDOS = subItemService.listByItemId(itemDO.getId());
        List<UserItemCollectDO> userItemCollectDOS = userItemCollectService.listByUserIdAndItemIdList(ContextHolder.getUserId(), Collections.singletonList(param.getId()));
        if (CollUtil.isEmpty(subItemDOS)) {
            return BeanUtil.copyToList(userItemCollectDOS, UserItemVO.class);
        }
        Map<Long, String> subItemVOMap = subItemDOS.stream().collect(Collectors.toMap(SubItemDO::getId, SubItemDO::getName));

        List<UserItemVO> userItemVOS = new ArrayList<>(Optional.ofNullable(BeanUtil.copyToList(userItemCollectDOS, UserItemVO.class)).orElse(List.of())
                .stream().peek(vo -> vo.setName("整盒")).toList());
        List<UserSubItemCollectDO> userSubItemCollectDOS = userSubItemCollectService.listByUserIdAndItemId(ContextHolder.getUserId(), param.getId());
        List<UserItemVO> userSubItemVOS = Optional.ofNullable(BeanUtil.copyToList(userSubItemCollectDOS, UserItemVO.class)).orElse(List.of())
                .stream().peek(vo -> vo.setName(subItemVOMap.get(vo.getSubItemId()))).toList();
        userItemVOS.addAll(userSubItemVOS);
        userItemVOS.sort(Comparator.comparing(UserItemVO::getCollectDate).reversed());
        return userItemVOS;
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
    @Transactional(rollbackFor = Exception.class)
    public void userSaveOrUpdate(UserItemParam param) {
        Long id = param.getId();
        if (ItemTypeEnum.ITEM.getCode().equals(param.getItemType())) {
            UserItemCollectDO userItemCollectDO = BeanUtil.copyProperties(param, UserItemCollectDO.class);
            userItemCollectDO.setUserId(ContextHolder.getUserId());
            userItemCollectService.saveOrUpdate(userItemCollectDO);
            if (Objects.isNull(id)) {
                itemCollectSummary(userItemCollectDO);
            }
        } else if (ItemTypeEnum.SUB_ITEM.getCode().equals(param.getItemType())) {
            UserSubItemCollectDO userItemCollectDO = BeanUtil.copyProperties(param, UserSubItemCollectDO.class);
            userItemCollectDO.setUserId(ContextHolder.getUserId());
            userSubItemCollectService.saveOrUpdate(userItemCollectDO);
            if (Objects.isNull(id)) {
                subItemCollectSummary(userItemCollectDO);
            }
        }
    }

    private void subItemCollectSummary(UserSubItemCollectDO userItemCollectDO) {
        UserItemSummaryDO userItemSummaryDO = userItemSummaryService.getByUserIdAndItemId(userItemCollectDO.getUserId(), userItemCollectDO.getItemId());
        List<SubItemDO> subItemDOS = subItemService.listByItemId(userItemCollectDO.getItemId());
        Set<Long> dbSubItemIdSet = subItemDOS.stream().map(SubItemDO::getId).collect(Collectors.toSet());
        if (Objects.nonNull(userItemSummaryDO)) {
            String subItemIdList = userItemSummaryDO.getSubItemIdList();
            Set<Long> subItemIdSet = new HashSet<>(JSONUtil.parseArray(subItemIdList).toList(Long.class));
            subItemIdSet.add(userItemCollectDO.getSubItemId());
            userItemSummaryDO.setSubItemIdList(JSONUtil.toJsonStr(subItemIdSet));

            if (subItemIdSet.equals(dbSubItemIdSet)) {
                userItemSummaryDO.setCollectStatus(true);
            }
            userItemSummaryService.updateById(userItemSummaryDO);
        } else {
            userItemSummaryDO = new UserItemSummaryDO();
            userItemSummaryDO.setUserId(userItemCollectDO.getUserId());
            userItemSummaryDO.setItemId(userItemCollectDO.getItemId());
            userItemSummaryDO.setCollectType(ItemTypeEnum.SUB_ITEM.getCode());
            Set<Long> subItemIdSet = new HashSet<>();
            subItemIdSet.add(userItemCollectDO.getSubItemId());
            userItemSummaryDO.setSubItemIdList(JSONUtil.toJsonStr(subItemIdSet));

            if (subItemIdSet.equals(dbSubItemIdSet)) {
                userItemSummaryDO.setCollectStatus(true);
            }
            userItemSummaryService.save(userItemSummaryDO);
        }
    }

    private void itemCollectSummary(UserItemCollectDO userItemCollectDO) {
        UserItemSummaryDO userItemSummaryDO = userItemSummaryService.getByUserIdAndItemId(userItemCollectDO.getUserId(), userItemCollectDO.getItemId());
        if (Objects.nonNull(userItemSummaryDO)) {
            userItemSummaryDO.setCollectStatus(true);
            userItemSummaryDO.setCollectType(ItemTypeEnum.ITEM.getCode());
            userItemSummaryService.updateById(userItemSummaryDO);
        } else {
            userItemSummaryDO = new UserItemSummaryDO();
            userItemSummaryDO.setUserId(userItemCollectDO.getUserId());
            userItemSummaryDO.setItemId(userItemCollectDO.getItemId());
            userItemSummaryDO.setCollectType(ItemTypeEnum.ITEM.getCode());
            userItemSummaryDO.setCollectStatus(true);
            userItemSummaryService.save(userItemSummaryDO);
        }
    }

    @Override
    public void userDelete(UserItemParam param) {
        if (Objects.isNull(param.getId()) || Objects.isNull(param.getItemType())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        Long itemId;
        if (ItemTypeEnum.ITEM.getCode().equals(param.getItemType())) {
            UserItemCollectDO userItemCollectDO = userItemCollectService.getById(param.getId());
            if (Objects.isNull(userItemCollectDO)) {
                throw ServiceException.instance(PARAM_ERROR);
            }
            itemId = userItemCollectDO.getItemId();
            userItemCollectService.removeById(param.getId());
        } else if (ItemTypeEnum.SUB_ITEM.getCode().equals(param.getItemType())) {
            UserSubItemCollectDO userSubItemCollectDO = userSubItemCollectService.getById(param.getId());
            if (Objects.isNull(userSubItemCollectDO)) {
                throw ServiceException.instance(PARAM_ERROR);
            }
            itemId = userSubItemCollectDO.getItemId();
            userSubItemCollectService.removeById(param.getId());
        } else {
            throw ServiceException.instance(PARAM_ERROR);
        }
        UserItemSummaryDO userItemSummaryDO = userItemSummaryService.getByUserIdAndItemId(param.getUserId(), itemId);

        List<SubItemDO> subItemDOS = subItemService.listByItemId(itemId);
        Set<Long> dbSubItemIdSet = subItemDOS.stream().map(SubItemDO::getId).collect(Collectors.toSet());
        List<UserSubItemCollectDO> userSubItemCollectDOS = userSubItemCollectService.listByUserIdAndItemId(param.getUserId(), itemId);
        Set<Long> dbCollectSubItemIdSet = userSubItemCollectDOS.stream().map(UserSubItemCollectDO::getSubItemId).collect(Collectors.toSet());

        userItemSummaryDO.setCollectType(ItemTypeEnum.SUB_ITEM.getCode());
        userItemSummaryDO.setSubItemIdList(JSONUtil.toJsonStr(dbCollectSubItemIdSet));
        userItemSummaryDO.setCollectStatus(dbCollectSubItemIdSet.equals(dbSubItemIdSet));

        List<UserItemCollectDO> userItemCollectDOS = userItemCollectService.listByUserIdAndItemIdList(param.getUserId(), Collections.singletonList(itemId));
        if (CollUtil.isNotEmpty(userItemCollectDOS)) {
            userItemSummaryDO.setCollectType(ItemTypeEnum.ITEM.getCode());
            userItemSummaryDO.setCollectStatus(true);
        }
        userItemSummaryService.updateById(userItemSummaryDO);
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

    public static void main(String[] args) {
        String subItemIdList = "[1,2,3,4,5,6,7,8,9,10,1]";
        Set<Long> subItemIdListSet = new HashSet<>(JSONUtil.parseArray(subItemIdList).toList(Long.class));
        System.out.println(subItemIdListSet);
    }
}








