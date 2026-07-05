package com.chuncongcong.sw.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.chuncongcong.framework.exception.ServiceException;
import com.chuncongcong.framework.util.ContextHolder;
import com.chuncongcong.sw.bean.param.PokemonParam;
import com.chuncongcong.sw.bean.param.UserPokemonCollectParam;
import com.chuncongcong.sw.bean.vo.PokemonScaleRecordVO;
import com.chuncongcong.sw.bean.vo.PokemonStatsVO;
import com.chuncongcong.sw.bean.vo.PokemonTypeVO;
import com.chuncongcong.sw.bean.vo.PokemonVO;
import com.chuncongcong.sw.bean.vo.UserPokemonCollectVO;
import com.chuncongcong.sw.biz.service.PokemonBizService;
import com.chuncongcong.sw.entity.CollectibleTypeDO;
import com.chuncongcong.sw.entity.ItemDO;
import com.chuncongcong.sw.entity.PokemonDO;
import com.chuncongcong.sw.entity.PokemonScaleMappingDO;
import com.chuncongcong.sw.entity.PokemonTypeDO;
import com.chuncongcong.sw.entity.SubItemDO;
import com.chuncongcong.sw.entity.UserItemCollectDO;
import com.chuncongcong.sw.entity.UserPokemonCollectDO;
import com.chuncongcong.sw.entity.UserSubItemCollectDO;
import com.chuncongcong.sw.service.CollectibleTypeService;
import com.chuncongcong.sw.service.ItemService;
import com.chuncongcong.sw.service.PokemonScaleMappingService;
import com.chuncongcong.sw.service.PokemonService;
import com.chuncongcong.sw.service.PokemonTypeService;
import com.chuncongcong.sw.service.SubItemService;
import com.chuncongcong.sw.service.UserItemCollectService;
import com.chuncongcong.sw.service.UserPokemonCollectService;
import com.chuncongcong.sw.service.UserSubItemCollectService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.chuncongcong.framework.exception.ServiceErrorEnum.PARAM_ERROR;

@Service
public class PokemonBizServiceImpl implements PokemonBizService {

    private static final String SOURCE_ITEM = "item";

    private static final String SOURCE_SUB_ITEM = "sub_item";

    private static final String COLLECTIBLE_TYPE_SCALE_WORLD = "scale_world";

    private static final String POKEMON_SOURCE_SCALE_ITEM = "scale_item";

    private static final String POKEMON_SOURCE_SCALE_SUB_ITEM = "scale_sub_item";

    @Resource
    private PokemonService pokemonService;
    @Resource
    private PokemonTypeService pokemonTypeService;
    @Resource
    private UserPokemonCollectService userPokemonCollectService;
    @Resource
    private CollectibleTypeService collectibleTypeService;
    @Resource
    private PokemonScaleMappingService pokemonScaleMappingService;
    @Resource
    private ItemService itemService;
    @Resource
    private SubItemService subItemService;
    @Resource
    private UserItemCollectService userItemCollectService;
    @Resource
    private UserSubItemCollectService userSubItemCollectService;

    @Override
    public List<PokemonVO> list(PokemonParam param) {
        if (Objects.isNull(param)) {
            param = new PokemonParam();
        }
        List<PokemonDO> pokemonList = pokemonService.listByRegionId(param.getRegionId(), param.getKeyword());
        if (CollUtil.isEmpty(pokemonList)) {
            return List.of();
        }
        List<PokemonVO> voList = toPokemonVOList(pokemonList);
        Long userId = ContextHolder.getUserId();
        if (Objects.isNull(userId)) {
            return voList;
        }
        List<Long> pokemonIds = voList.stream().map(PokemonVO::getId).toList();
        List<UserPokemonCollectDO> collectList = userPokemonCollectService.listByUserIdAndPokemonIds(userId, pokemonIds);
        Map<Long, List<UserPokemonCollectDO>> collectMap = collectList.stream()
                .collect(Collectors.groupingBy(UserPokemonCollectDO::getPokemonId));
        for (PokemonVO vo : voList) {
            List<UserPokemonCollectDO> records = collectMap.getOrDefault(vo.getId(), List.of());
            vo.setCollect(CollUtil.isNotEmpty(records));
            vo.setCollectRecordCount(records.size());
            vo.setCollectPriceTotal(collectPriceTotal(records));
        }
        Boolean collectedOnly = param.getCollectedOnly();
        if (Objects.nonNull(collectedOnly)) {
            return voList.stream()
                    .filter(vo -> Objects.equals(collectedOnly, vo.getCollect()))
                    .toList();
        }
        return voList;
    }

    @Override
    public PokemonVO info(PokemonParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getId())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        PokemonDO pokemonDO = pokemonService.getById(param.getId());
        if (Objects.isNull(pokemonDO)) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        PokemonVO vo = toPokemonVO(pokemonDO);
        vo.setCollectList(userCollectList(buildCollectParam(pokemonDO.getId())));
        vo.setScaleRecordList(scaleRecordList(pokemonDO.getId()));
        vo.setCollect(CollUtil.isNotEmpty(vo.getCollectList()));
        vo.setCollectRecordCount(vo.getCollectList().size());
        return vo;
    }

    @Override
    public List<UserPokemonCollectVO> userCollectList(UserPokemonCollectParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getPokemonId())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        Long userId = ContextHolder.getUserId();
        if (Objects.isNull(userId)) {
            return List.of();
        }
        Map<String, String> typeNameMap = collectibleTypes().stream()
                .collect(Collectors.toMap(CollectibleTypeDO::getCode, CollectibleTypeDO::getName));
        List<UserPokemonCollectDO> records = userPokemonCollectService.listByUserIdAndPokemonId(userId, param.getPokemonId());
        Map<String, String> sourceNameMap = scaleWorldSourceNameMap(records);
        return records.stream()
                .map(record -> {
                    UserPokemonCollectVO vo = BeanUtil.toBean(record, UserPokemonCollectVO.class);
                    vo.setCollectibleTypeName(typeNameMap.get(record.getCollectibleTypeCode()));
                    vo.setSourceName(sourceNameMap.get(sourceKey(record.getSourceType(), record.getSourceId())));
                    return vo;
                })
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userSaveOrUpdate(UserPokemonCollectParam param) {
        if (Objects.isNull(param)
                || Objects.isNull(param.getPokemonId())
                || CharSequenceUtil.isBlank(param.getCollectibleTypeCode())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        if (Objects.isNull(pokemonService.getById(param.getPokemonId()))) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        boolean validType = collectibleTypes().stream()
                .anyMatch(type -> Objects.equals(type.getCode(), param.getCollectibleTypeCode()));
        if (!validType) {
            throw ServiceException.instance(PARAM_ERROR);
        }

        UserPokemonCollectDO record = BeanUtil.copyProperties(param, UserPokemonCollectDO.class);
        record.setUserId(ContextHolder.getUserId());
        if (Objects.nonNull(record.getId())) {
            UserPokemonCollectDO dbRecord = userPokemonCollectService.getById(record.getId());
            if (Objects.isNull(dbRecord) || !Objects.equals(dbRecord.getUserId(), record.getUserId())) {
                throw ServiceException.instance(PARAM_ERROR);
            }
        }
        userPokemonCollectService.saveOrUpdate(record);
    }

    @Override
    public void userDelete(UserPokemonCollectParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getId())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        UserPokemonCollectDO record = userPokemonCollectService.getById(param.getId());
        if (Objects.isNull(record) || !Objects.equals(record.getUserId(), ContextHolder.getUserId())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        userPokemonCollectService.removeById(record.getId());
    }

    @Override
    public void userExchange(UserPokemonCollectParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getId()) || Objects.isNull(param.getExchange())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        UserPokemonCollectDO record = userPokemonCollectService.getById(param.getId());
        if (Objects.isNull(record) || !Objects.equals(record.getUserId(), ContextHolder.getUserId())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        record.setExchange(param.getExchange());
        record.setExchangeRemark(Boolean.TRUE.equals(param.getExchange()) ? param.getExchangeRemark() : null);
        userPokemonCollectService.updateById(record);
    }

    @Override
    public PokemonStatsVO stats(PokemonParam param) {
        if (Objects.isNull(param)) {
            param = new PokemonParam();
        }
        List<PokemonDO> pokemonList = pokemonService.listByRegionId(param.getRegionId(), null);
        PokemonStatsVO stats = new PokemonStatsVO();
        stats.setRegionId(param.getRegionId());
        stats.setCount(pokemonList.size());
        Long userId = ContextHolder.getUserId();
        if (Objects.isNull(userId) || CollUtil.isEmpty(pokemonList)) {
            return stats;
        }
        List<Long> pokemonIds = pokemonList.stream().map(PokemonDO::getId).toList();
        List<UserPokemonCollectDO> collectList = userPokemonCollectService.listByUserIdAndPokemonIds(userId, pokemonIds);
        Set<Long> collectedPokemonIds = collectList.stream().map(UserPokemonCollectDO::getPokemonId).collect(Collectors.toSet());
        stats.setCollectCount(collectedPokemonIds.size());
        stats.setCollectNumber(collectList.size());
        stats.setCollectPriceTotal(collectPriceTotal(collectList));
        if (stats.getCount() > 0) {
            stats.setCollectRate(BigDecimal.valueOf(stats.getCollectCount())
                    .divide(BigDecimal.valueOf(stats.getCount()), 4, RoundingMode.HALF_UP));
        }
        return stats;
    }

    @Override
    public List<CollectibleTypeDO> collectibleTypes() {
        return collectibleTypeService.listEnabled();
    }

    @Override
    public List<PokemonTypeVO> pokemonTypes() {
        return pokemonTypeService.listAll().stream()
                .map(this::toPokemonTypeVO)
                .toList();
    }

    private UserPokemonCollectParam buildCollectParam(Long pokemonId) {
        UserPokemonCollectParam param = new UserPokemonCollectParam();
        param.setPokemonId(pokemonId);
        return param;
    }

    private List<PokemonVO> toPokemonVOList(List<PokemonDO> pokemonList) {
        Map<String, PokemonTypeDO> pokemonTypeMap = pokemonTypeMap();
        return pokemonList.stream().map(pokemon -> toPokemonVO(pokemon, pokemonTypeMap)).toList();
    }

    private PokemonVO toPokemonVO(PokemonDO pokemonDO) {
        return toPokemonVO(pokemonDO, pokemonTypeMap());
    }

    private PokemonVO toPokemonVO(PokemonDO pokemonDO, Map<String, PokemonTypeDO> pokemonTypeMap) {
        PokemonVO vo = BeanUtil.toBean(pokemonDO, PokemonVO.class);
        vo.setImg(pokemonDO.getLocalImg());
        vo.setTypeList(parseTypeCodes(pokemonDO.getTypeNames()).stream()
                .map(pokemonTypeMap::get)
                .filter(Objects::nonNull)
                .map(this::toPokemonTypeVO)
                .toList());
        return vo;
    }

    private Map<String, PokemonTypeDO> pokemonTypeMap() {
        return pokemonTypeService.listAll().stream()
                .collect(Collectors.toMap(PokemonTypeDO::getCode, Function.identity(), (left, right) -> left));
    }

    private List<String> parseTypeCodes(String typeNames) {
        if (CharSequenceUtil.isBlank(typeNames)) {
            return List.of();
        }
        LinkedHashSet<String> codes = new LinkedHashSet<>();
        try {
            JSONArray array = JSONUtil.parseArray(typeNames);
            for (Object value : array) {
                String code = parseTypeCode(value);
                if (CharSequenceUtil.isNotBlank(code)) {
                    codes.add(code);
                }
            }
        } catch (Exception ignored) {
            for (String item : typeNames.split(",")) {
                String code = item.trim();
                if (CharSequenceUtil.isNotBlank(code)) {
                    codes.add(code);
                }
            }
        }
        return codes.stream().toList();
    }

    private Map<String, String> scaleWorldSourceNameMap(List<UserPokemonCollectDO> records) {
        if (CollUtil.isEmpty(records)) {
            return Map.of();
        }
        List<Long> itemIds = records.stream()
                .filter(record -> isScaleWorldCollect(record) && POKEMON_SOURCE_SCALE_ITEM.equals(record.getSourceType()))
                .map(UserPokemonCollectDO::getSourceId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        List<Long> subItemIds = records.stream()
                .filter(record -> isScaleWorldCollect(record) && POKEMON_SOURCE_SCALE_SUB_ITEM.equals(record.getSourceType()))
                .map(UserPokemonCollectDO::getSourceId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<String, String> result = new HashMap<>();
        if (CollUtil.isNotEmpty(itemIds)) {
            itemService.listByIds(itemIds).forEach(item ->
                    result.put(sourceKey(POKEMON_SOURCE_SCALE_ITEM, item.getId()), item.getName()));
        }
        if (CollUtil.isNotEmpty(subItemIds)) {
            subItemService.listByIds(subItemIds).forEach(subItem ->
                    result.put(sourceKey(POKEMON_SOURCE_SCALE_SUB_ITEM, subItem.getId()), subItem.getName()));
        }
        return result;
    }

    private BigDecimal collectPriceTotal(List<UserPokemonCollectDO> records) {
        if (CollUtil.isEmpty(records)) {
            return BigDecimal.ZERO;
        }
        Set<String> countedScaleWorldSources = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;
        for (UserPokemonCollectDO record : records) {
            if (Objects.isNull(record.getCollectPrice())) {
                continue;
            }
            String costSourceKey = scaleWorldCostSourceKey(record);
            if (Objects.nonNull(costSourceKey) && !countedScaleWorldSources.add(costSourceKey)) {
                continue;
            }
            total = total.add(record.getCollectPrice());
        }
        return total;
    }

    private String scaleWorldCostSourceKey(UserPokemonCollectDO record) {
        if (!isScaleWorldCollect(record)
                || CharSequenceUtil.isBlank(record.getSourceType())
                || Objects.isNull(record.getSourceCollectId())) {
            return null;
        }
        return record.getSourceType() + ":" + record.getSourceCollectId();
    }
    private boolean isScaleWorldCollect(UserPokemonCollectDO record) {
        return Objects.equals(COLLECTIBLE_TYPE_SCALE_WORLD, record.getCollectibleTypeCode());
    }

    private String sourceKey(String sourceType, Long sourceId) {
        if (CharSequenceUtil.isBlank(sourceType) || Objects.isNull(sourceId)) {
            return null;
        }
        return sourceType + ":" + sourceId;
    }

    private String parseTypeCode(Object value) {
        if (Objects.isNull(value)) {
            return null;
        }
        if (value instanceof CharSequence charSequence) {
            return charSequence.toString();
        }
        JSONObject object = JSONUtil.parseObj(value);
        String typeName = object.getByPath("type.name", String.class);
        if (CharSequenceUtil.isNotBlank(typeName)) {
            return typeName;
        }
        return object.getStr("name");
    }

    private PokemonTypeVO toPokemonTypeVO(PokemonTypeDO pokemonTypeDO) {
        PokemonTypeVO vo = BeanUtil.toBean(pokemonTypeDO, PokemonTypeVO.class);
        vo.setIcon(pokemonTypeDO.getLocalIcon());
        return vo;
    }

    private List<PokemonScaleRecordVO> scaleRecordList(Long pokemonId) {
        List<PokemonScaleMappingDO> mappings = pokemonScaleMappingService.listConfirmedByPokemonId(pokemonId);
        if (CollUtil.isEmpty(mappings)) {
            return List.of();
        }
        Map<Long, ItemDO> itemMap = loadMappedItemMap(mappings);
        Map<Long, SubItemDO> subItemMap = loadMappedSubItemMap(mappings);
        Map<Long, UserItemCollectDO> itemCollectMap = loadUserItemCollectMap(itemMap.keySet());
        Map<Long, UserSubItemCollectDO> subItemCollectMap = loadUserSubItemCollectMap(subItemMap.keySet());

        List<PokemonScaleRecordVO> result = new ArrayList<>();
        for (PokemonScaleMappingDO mapping : mappings) {
            if (SOURCE_ITEM.equals(mapping.getSourceType())) {
                ItemDO item = itemMap.get(mapping.getSourceId());
                if (Objects.nonNull(item)) {
                    result.add(toScaleRecord(mapping, item, itemCollectMap.get(item.getId())));
                }
            } else if (SOURCE_SUB_ITEM.equals(mapping.getSourceType())) {
                SubItemDO subItem = subItemMap.get(mapping.getSourceId());
                if (Objects.nonNull(subItem)) {
                    result.add(toScaleRecord(mapping, subItem, subItemCollectMap.get(subItem.getId())));
                }
            }
        }
        return result;
    }

    private Map<Long, ItemDO> loadMappedItemMap(List<PokemonScaleMappingDO> mappings) {
        List<Long> ids = mappings.stream()
                .filter(mapping -> SOURCE_ITEM.equals(mapping.getSourceType()))
                .map(PokemonScaleMappingDO::getSourceId)
                .toList();
        if (CollUtil.isEmpty(ids)) {
            return Map.of();
        }
        return itemService.listByIds(ids).stream().collect(Collectors.toMap(ItemDO::getId, Function.identity()));
    }

    private Map<Long, SubItemDO> loadMappedSubItemMap(List<PokemonScaleMappingDO> mappings) {
        List<Long> ids = mappings.stream()
                .filter(mapping -> SOURCE_SUB_ITEM.equals(mapping.getSourceType()))
                .map(PokemonScaleMappingDO::getSourceId)
                .toList();
        if (CollUtil.isEmpty(ids)) {
            return Map.of();
        }
        return subItemService.listByIds(ids).stream().collect(Collectors.toMap(SubItemDO::getId, Function.identity()));
    }

    private Map<Long, UserItemCollectDO> loadUserItemCollectMap(Set<Long> itemIds) {
        Long userId = ContextHolder.getUserId();
        if (Objects.isNull(userId) || CollUtil.isEmpty(itemIds)) {
            return Map.of();
        }
        return userItemCollectService.listByUserIdAndItemIdList(userId, itemIds.stream().toList()).stream()
                .collect(Collectors.toMap(
                        UserItemCollectDO::getItemId,
                        Function.identity(),
                        (left, right) -> compareCollect(left, right) <= 0 ? left : right
                ));
    }

    private Map<Long, UserSubItemCollectDO> loadUserSubItemCollectMap(Set<Long> subItemIds) {
        Long userId = ContextHolder.getUserId();
        if (Objects.isNull(userId) || CollUtil.isEmpty(subItemIds)) {
            return Map.of();
        }
        Map<Long, UserSubItemCollectDO> result = new HashMap<>();
        List<UserSubItemCollectDO> userSubItems = userSubItemCollectService.listByUserId(userId);
        for (UserSubItemCollectDO collect : userSubItems) {
            if (CharSequenceUtil.isBlank(collect.getSubItemId())) {
                continue;
            }
            List<Long> ids = JSONUtil.parseArray(collect.getSubItemId()).toList(Long.class);
            for (Long id : ids) {
                if (!subItemIds.contains(id)) {
                    continue;
                }
                UserSubItemCollectDO existing = result.get(id);
                if (Objects.isNull(existing) || compareCollect(collect, existing) < 0) {
                    result.put(id, collect);
                }
            }
        }
        return result;
    }

    private PokemonScaleRecordVO toScaleRecord(PokemonScaleMappingDO mapping, ItemDO item, UserItemCollectDO collect) {
        PokemonScaleRecordVO vo = new PokemonScaleRecordVO();
        vo.setSourceId(mapping.getSourceId());
        vo.setSourceType(mapping.getSourceType());
        vo.setItemName(item.getName());
        vo.setItemImg(item.getImg());
        fillScaleCollect(vo, collect);
        return vo;
    }

    private PokemonScaleRecordVO toScaleRecord(PokemonScaleMappingDO mapping, SubItemDO subItem, UserSubItemCollectDO collect) {
        PokemonScaleRecordVO vo = new PokemonScaleRecordVO();
        vo.setSourceId(mapping.getSourceId());
        vo.setSourceType(mapping.getSourceType());
        vo.setItemName(subItem.getName());
        vo.setItemImg(subItem.getImg());
        fillScaleCollect(vo, collect);
        return vo;
    }

    private void fillScaleCollect(PokemonScaleRecordVO vo, UserItemCollectDO collect) {
        if (Objects.isNull(collect)) {
            return;
        }
        vo.setCollect(true);
        vo.setCollectDate(collect.getCollectDate());
        vo.setCollectPrice(collect.getCollectPrice());
        vo.setRemark(collect.getRemark());
    }

    private void fillScaleCollect(PokemonScaleRecordVO vo, UserSubItemCollectDO collect) {
        if (Objects.isNull(collect)) {
            return;
        }
        vo.setCollect(true);
        vo.setCollectDate(collect.getCollectDate());
        vo.setCollectPrice(collect.getCollectPrice());
        vo.setRemark(collect.getRemark());
    }

    private int compareCollect(UserItemCollectDO left, UserItemCollectDO right) {
        return Comparator
                .comparing(UserItemCollectDO::getCollectDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .compare(left, right);
    }

    private int compareCollect(UserSubItemCollectDO left, UserSubItemCollectDO right) {
        return Comparator
                .comparing(UserSubItemCollectDO::getCollectDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .compare(left, right);
    }
}