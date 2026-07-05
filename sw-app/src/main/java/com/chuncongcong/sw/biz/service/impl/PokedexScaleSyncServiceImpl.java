package com.chuncongcong.sw.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.chuncongcong.framework.exception.ServiceException;
import com.chuncongcong.sw.bean.vo.PokedexScaleSyncResultVO;
import com.chuncongcong.sw.biz.service.PokedexScaleSyncService;
import com.chuncongcong.sw.entity.PokemonScaleMappingDO;
import com.chuncongcong.sw.entity.UserDO;
import com.chuncongcong.sw.entity.UserItemCollectDO;
import com.chuncongcong.sw.entity.UserPokemonCollectDO;
import com.chuncongcong.sw.entity.UserSubItemCollectDO;
import com.chuncongcong.sw.service.PokemonScaleMappingService;
import com.chuncongcong.sw.service.UserItemCollectService;
import com.chuncongcong.sw.service.UserPokemonCollectService;
import com.chuncongcong.sw.service.UserService;
import com.chuncongcong.sw.service.UserSubItemCollectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.chuncongcong.framework.exception.ServiceErrorEnum.PARAM_ERROR;

@Service
public class PokedexScaleSyncServiceImpl implements PokedexScaleSyncService {

    public static final int SYNC_STATUS_SYNCED = 1;

    private static final String COLLECTIBLE_TYPE_SCALE_WORLD = "scale_world";

    private static final String MAPPING_SOURCE_ITEM = "item";

    private static final String MAPPING_SOURCE_SUB_ITEM = "sub_item";

    private static final String POKEMON_SOURCE_SCALE_ITEM = "scale_item";

    private static final String POKEMON_SOURCE_SCALE_SUB_ITEM = "scale_sub_item";

    private final UserService userService;

    private final UserItemCollectService userItemCollectService;

    private final UserSubItemCollectService userSubItemCollectService;

    private final UserPokemonCollectService userPokemonCollectService;

    private final PokemonScaleMappingService pokemonScaleMappingService;

    public PokedexScaleSyncServiceImpl(UserService userService,
                                       UserItemCollectService userItemCollectService,
                                       UserSubItemCollectService userSubItemCollectService,
                                       UserPokemonCollectService userPokemonCollectService,
                                       PokemonScaleMappingService pokemonScaleMappingService) {
        this.userService = userService;
        this.userItemCollectService = userItemCollectService;
        this.userSubItemCollectService = userSubItemCollectService;
        this.userPokemonCollectService = userPokemonCollectService;
        this.pokemonScaleMappingService = pokemonScaleMappingService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PokedexScaleSyncResultVO syncUserScaleCollects(Long userId) {
        if (Objects.isNull(userId)) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        UserDO user = userService.getById(userId);
        if (Objects.isNull(user)) {
            throw ServiceException.instance(PARAM_ERROR);
        }

        PokedexScaleSyncResultVO result = new PokedexScaleSyncResultVO();
        userPokemonCollectService.removeScaleWorldSourcesByUserId(userId);

        List<UserPokemonCollectDO> records = new ArrayList<>();
        records.addAll(buildItemCollects(userItemCollectService.listByUserId(userId), result));
        records.addAll(buildSubItemCollects(userSubItemCollectService.listByUserId(userId), result));
        if (CollUtil.isNotEmpty(records)) {
            userPokemonCollectService.saveBatch(records);
        }

        user.setPokemonCollectSyncStatus(SYNC_STATUS_SYNCED);
        user.setPokemonCollectSyncTime(LocalDateTime.now());
        user.setPokemonCollectSyncError(null);
        userService.updateById(user);
        result.setSyncStatus(SYNC_STATUS_SYNCED);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncItemCollect(UserItemCollectDO record) {
        if (Objects.isNull(record) || Objects.isNull(record.getUserId()) || Objects.isNull(record.getId())) {
            return;
        }
        deleteItemCollectSource(record.getUserId(), record.getId());
        PokedexScaleSyncResultVO result = new PokedexScaleSyncResultVO();
        List<UserPokemonCollectDO> records = buildItemCollects(List.of(record), result);
        if (CollUtil.isNotEmpty(records)) {
            userPokemonCollectService.saveBatch(records);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncSubItemCollect(UserSubItemCollectDO record) {
        if (Objects.isNull(record) || Objects.isNull(record.getUserId()) || Objects.isNull(record.getId())) {
            return;
        }
        deleteSubItemCollectSource(record.getUserId(), record.getId());
        PokedexScaleSyncResultVO result = new PokedexScaleSyncResultVO();
        List<UserPokemonCollectDO> records = buildSubItemCollects(List.of(record), result);
        if (CollUtil.isNotEmpty(records)) {
            userPokemonCollectService.saveBatch(records);
        }
    }

    @Override
    public void deleteItemCollectSource(Long userId, Long sourceCollectId) {
        userPokemonCollectService.removeScaleWorldSource(userId, POKEMON_SOURCE_SCALE_ITEM, sourceCollectId);
    }

    @Override
    public void deleteSubItemCollectSource(Long userId, Long sourceCollectId) {
        userPokemonCollectService.removeScaleWorldSource(userId, POKEMON_SOURCE_SCALE_SUB_ITEM, sourceCollectId);
    }

    private List<UserPokemonCollectDO> buildItemCollects(List<UserItemCollectDO> itemCollects, PokedexScaleSyncResultVO result) {
        if (CollUtil.isEmpty(itemCollects)) {
            return List.of();
        }
        List<Long> itemIds = itemCollects.stream()
                .map(UserItemCollectDO::getItemId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, List<PokemonScaleMappingDO>> mappingMap = pokemonScaleMappingService.listBySource(MAPPING_SOURCE_ITEM, itemIds).stream()
                .collect(Collectors.groupingBy(PokemonScaleMappingDO::getSourceId));

        List<UserPokemonCollectDO> records = new ArrayList<>();
        for (UserItemCollectDO itemCollect : itemCollects) {
            List<PokemonScaleMappingDO> mappings = mappingMap.get(itemCollect.getItemId());
            if (CollUtil.isEmpty(mappings)) {
                result.incrementSkipNoMappingCount();
                continue;
            }
            for (PokemonScaleMappingDO mapping : mappings) {
                records.add(toPokemonCollect(itemCollect, mapping));
                result.incrementInsertCount();
            }
        }
        return records;
    }

    private List<UserPokemonCollectDO> buildSubItemCollects(List<UserSubItemCollectDO> subItemCollects, PokedexScaleSyncResultVO result) {
        if (CollUtil.isEmpty(subItemCollects)) {
            return List.of();
        }
        List<Long> subItemIds = subItemCollects.stream()
                .flatMap(record -> parseSubItemIds(record).stream())
                .distinct()
                .toList();
        Map<Long, List<PokemonScaleMappingDO>> mappingMap = pokemonScaleMappingService.listBySource(MAPPING_SOURCE_SUB_ITEM, subItemIds).stream()
                .collect(Collectors.groupingBy(PokemonScaleMappingDO::getSourceId));

        List<UserPokemonCollectDO> records = new ArrayList<>();
        for (UserSubItemCollectDO subItemCollect : subItemCollects) {
            for (Long subItemId : parseSubItemIds(subItemCollect)) {
                List<PokemonScaleMappingDO> mappings = mappingMap.get(subItemId);
                if (CollUtil.isEmpty(mappings)) {
                    result.incrementSkipNoMappingCount();
                    continue;
                }
                for (PokemonScaleMappingDO mapping : mappings) {
                    records.add(toPokemonCollect(subItemCollect, subItemId, mapping));
                    result.incrementInsertCount();
                }
            }
        }
        return records;
    }

    private List<Long> parseSubItemIds(UserSubItemCollectDO record) {
        if (Objects.isNull(record) || CharSequenceUtil.isBlank(record.getSubItemId())) {
            return List.of();
        }
        return JSONUtil.parseArray(record.getSubItemId()).toList(Long.class);
    }

    private UserPokemonCollectDO toPokemonCollect(UserItemCollectDO source, PokemonScaleMappingDO mapping) {
        UserPokemonCollectDO record = new UserPokemonCollectDO();
        fillBaseCollect(record, source.getUserId(), mapping.getPokemonId(), source.getCollectDate(), source.getCollectPrice(),
                source.getRemark());
        record.setSourceType(POKEMON_SOURCE_SCALE_ITEM);
        record.setSourceCollectId(source.getId());
        record.setSourceId(source.getItemId());
        return record;
    }

    private UserPokemonCollectDO toPokemonCollect(UserSubItemCollectDO source, Long subItemId, PokemonScaleMappingDO mapping) {
        UserPokemonCollectDO record = new UserPokemonCollectDO();
        fillBaseCollect(record, source.getUserId(), mapping.getPokemonId(), source.getCollectDate(), source.getCollectPrice(),
                source.getRemark());
        record.setSourceType(POKEMON_SOURCE_SCALE_SUB_ITEM);
        record.setSourceCollectId(source.getId());
        record.setSourceId(subItemId);
        return record;
    }

    private void fillBaseCollect(UserPokemonCollectDO record,
                                 Long userId,
                                 Long pokemonId,
                                 java.time.LocalDate collectDate,
                                 java.math.BigDecimal collectPrice,
                                 String remark) {
        record.setUserId(userId);
        record.setPokemonId(pokemonId);
        record.setCollectibleTypeCode(COLLECTIBLE_TYPE_SCALE_WORLD);
        record.setCollectDate(collectDate);
        record.setCollectPrice(collectPrice);
        record.setRemark(remark);
    }
}
