package com.chuncongcong.sw.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.chuncongcong.framework.exception.ServiceException;
import com.chuncongcong.framework.util.ContextHolder;
import com.chuncongcong.sw.bean.enums.ItemTypeEnum;
import com.chuncongcong.sw.bean.enums.ReservationPaymentTypeEnum;
import com.chuncongcong.sw.bean.param.ReservationConvertParam;
import com.chuncongcong.sw.bean.param.ReservationParam;
import com.chuncongcong.sw.bean.param.UserItemParam;
import com.chuncongcong.sw.bean.vo.ReservationVO;
import com.chuncongcong.sw.biz.service.ItemBizService;
import com.chuncongcong.sw.biz.service.ReservationBizService;
import com.chuncongcong.sw.entity.ItemDO;
import com.chuncongcong.sw.entity.ReservationDO;
import com.chuncongcong.sw.entity.SubItemDO;
import com.chuncongcong.sw.service.ItemService;
import com.chuncongcong.sw.service.ReservationService;
import com.chuncongcong.sw.service.SubItemService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReservationBizServiceImpl implements ReservationBizService {

    private static final int STATUS_ACTIVE = 1;
    private static final int STATUS_CONVERTED = 2;
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Resource
    private ReservationService reservationService;
    @Resource
    private ItemService itemService;
    @Resource
    private SubItemService subItemService;
    @Resource
    private ItemBizService itemBizService;

    @Override
    public List<ReservationVO> list() {
        List<ReservationDO> reservations = reservationService.listActiveByUserId(ContextHolder.getUserId());
        if (CollUtil.isEmpty(reservations)) {
            return List.of();
        }

        Map<Long, ItemDO> itemMap = itemService.listByIds(
                        reservations.stream().map(ReservationDO::getItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ItemDO::getId, Function.identity()));

        Set<Long> subItemIds = reservations.stream()
                .filter(item -> ItemTypeEnum.SUB_ITEM.getCode().equals(item.getItemType()))
                .flatMap(item -> parseSubItemIds(item.getSubItemId()).stream())
                .collect(Collectors.toSet());
        Map<Long, SubItemDO> subItemMap = CollUtil.isEmpty(subItemIds)
                ? Collections.emptyMap()
                : subItemService.listByIds(subItemIds).stream()
                .collect(Collectors.toMap(SubItemDO::getId, Function.identity()));

        List<ReservationVO> result = new ArrayList<>(reservations.size());
        for (ReservationDO reservation : reservations) {
            ItemDO item = itemMap.get(reservation.getItemId());
            ReservationVO vo = toVO(reservation, item, subItemMap);
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(ReservationParam param) {
        validate(param);
        Long userId = ContextHolder.getUserId();
        ReservationDO reservation = new ReservationDO();
        if (param.getId() != null) {
            reservation = requireOwnedActive(param.getId(), userId);
        }

        reservation.setUserId(userId);
        reservation.setItemType(param.getItemType());
        reservation.setItemId(param.getItemId());
        reservation.setSubItemId(ItemTypeEnum.SUB_ITEM.getCode().equals(param.getItemType())
                ? JSONUtil.toJsonStr(param.getSubItemIdList()) : null);
        reservation.setReservationType(param.getReservationType());
        reservation.setReservationAmount(param.getReservationAmount());
        boolean deposit = ReservationPaymentTypeEnum.DEPOSIT.getCode().equals(param.getReservationType());
        reservation.setBalanceAmount(deposit ? param.getBalanceAmount() : null);
        reservation.setBalanceDueDate(deposit ? param.getBalanceDueDate() : null);
        reservation.setArrivalDate(param.getArrivalDate());
        reservation.setStatus(STATUS_ACTIVE);
        reservationService.saveOrUpdate(reservation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ReservationDO reservation = requireOwnedActive(id, ContextHolder.getUserId());
        reservationService.removeById(reservation.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void convertToCollect(ReservationConvertParam param) {
        if (param == null || param.getId() == null || param.getCollectPrice() == null
                || param.getCollectPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException(400, "请输入正确的收藏价格");
        }
        ReservationDO reservation = reservationService.lambdaQuery()
                .eq(ReservationDO::getId, param.getId())
                .eq(ReservationDO::getUserId, ContextHolder.getUserId())
                .eq(ReservationDO::getStatus, STATUS_ACTIVE)
                .last("FOR UPDATE")
                .one();
        if (reservation == null) {
            throw new ServiceException(400, "预定记录不存在或已处理");
        }

        UserItemParam collectParam = new UserItemParam();
        collectParam.setItemType(reservation.getItemType());
        collectParam.setItemId(reservation.getItemId());
        collectParam.setCollectDate(LocalDate.now());
        collectParam.setCollectPrice(param.getCollectPrice());
        if (ItemTypeEnum.SUB_ITEM.getCode().equals(reservation.getItemType())) {
            collectParam.setSubItemIdList(parseSubItemIds(reservation.getSubItemId()));
        }
        itemBizService.userSaveOrUpdate(collectParam);

        reservation.setStatus(STATUS_CONVERTED);
        reservation.setConvertedTime(LocalDateTime.now());
        reservationService.updateById(reservation);
    }

    private void validate(ReservationParam param) {
        if (param == null || param.getItemId() == null || param.getItemType() == null
                || param.getReservationType() == null || param.getReservationAmount() == null
                || param.getArrivalDate() == null || param.getReservationAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException(400, "请完整填写预定信息");
        }
        boolean isMainItem = ItemTypeEnum.ITEM.getCode().equals(param.getItemType());
        boolean isSubItem = ItemTypeEnum.SUB_ITEM.getCode().equals(param.getItemType());
        if (!isMainItem && !isSubItem) {
            throw new ServiceException(400, "项目类型不正确");
        }
        if (!ReservationPaymentTypeEnum.FULL_PAYMENT.getCode().equals(param.getReservationType())
                && !ReservationPaymentTypeEnum.DEPOSIT.getCode().equals(param.getReservationType())) {
            throw new ServiceException(400, "预定类型不正确");
        }
        if (ReservationPaymentTypeEnum.DEPOSIT.getCode().equals(param.getReservationType())
                && param.getBalanceAmount() != null
                && param.getBalanceAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException(400, "尾款金额不能小于0");
        }
        if (ReservationPaymentTypeEnum.DEPOSIT.getCode().equals(param.getReservationType())
                && param.getBalanceDueDate() != null
                && param.getBalanceDueDate().isAfter(param.getArrivalDate())) {
            throw new ServiceException(400, "付尾款时间不能晚于到货时间");
        }

        ItemDO item = itemService.getById(param.getItemId());
        if (item == null) {
            throw new ServiceException(400, "所选主项目不存在");
        }
        if (isSubItem) {
            validateSubItems(param.getItemId(), param.getSubItemIdList());
        }
    }

    private void validateSubItems(Long itemId, List<Long> selectedIds) {
        if (CollUtil.isEmpty(selectedIds)) {
            throw new ServiceException(400, "请选择子项目");
        }
        Set<Long> uniqueIds = new HashSet<>(selectedIds);
        List<SubItemDO> subItems = subItemService.listByIds(uniqueIds);
        boolean allBelongToItem = subItems.size() == uniqueIds.size()
                && subItems.stream().allMatch(subItem -> Objects.equals(itemId, subItem.getItemId()));
        if (!allBelongToItem) {
            throw new ServiceException(400, "所选子项目不存在或不属于该主项目");
        }
    }

    private ReservationDO requireOwnedActive(Long id, Long userId) {
        if (id == null) {
            throw new ServiceException(400, "预定记录不存在");
        }
        ReservationDO reservation = reservationService.getById(id);
        if (reservation == null || !Objects.equals(userId, reservation.getUserId())
                || !Objects.equals(STATUS_ACTIVE, reservation.getStatus())) {
            throw new ServiceException(400, "预定记录不存在或已处理");
        }
        return reservation;
    }

    private ReservationVO toVO(ReservationDO reservation, ItemDO item, Map<Long, SubItemDO> subItemMap) {
        ReservationVO vo = new ReservationVO();
        vo.setId(reservation.getId());
        vo.setItemType(reservation.getItemType());
        vo.setItemId(reservation.getItemId());
        vo.setReservationType(reservation.getReservationType());
        vo.setReservationAmount(reservation.getReservationAmount());
        vo.setBalanceAmount(reservation.getBalanceAmount());
        vo.setBalanceDueDate(reservation.getBalanceDueDate());
        vo.setArrivalDate(reservation.getArrivalDate());
        vo.setArrivalMonth(reservation.getArrivalDate().format(MONTH_FORMATTER));

        if (ItemTypeEnum.SUB_ITEM.getCode().equals(reservation.getItemType())) {
            List<Long> ids = parseSubItemIds(reservation.getSubItemId());
            List<SubItemDO> selected = ids.stream().map(subItemMap::get).filter(Objects::nonNull).toList();
            vo.setSubItemIdList(ids);
            vo.setItemName(selected.isEmpty()
                    ? (item == null ? "子项目" : item.getName())
                    : selected.stream().map(SubItemDO::getName).collect(Collectors.joining("、")));
            vo.setItemImg(selected.isEmpty() ? (item == null ? null : item.getImg()) : selected.get(0).getImg());
        } else {
            vo.setSubItemIdList(List.of());
            vo.setItemName(item == null ? "主项目" : item.getName());
            vo.setItemImg(item == null ? null : item.getImg());
        }
        return vo;
    }

    private List<Long> parseSubItemIds(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return JSONUtil.parseArray(json).toList(Long.class);
        } catch (RuntimeException ignored) {
            return List.of();
        }
    }
}
