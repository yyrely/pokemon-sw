package com.chuncongcong.sw.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.chuncongcong.framework.exception.ServiceException;
import com.chuncongcong.sw.bean.param.RegionParam;
import com.chuncongcong.sw.bean.vo.ReginItemCountVO;
import com.chuncongcong.sw.bean.vo.RegionVO;
import com.chuncongcong.sw.biz.service.ItemBizService;
import com.chuncongcong.sw.biz.service.ReginBizService;
import com.chuncongcong.sw.entity.RegionDO;
import com.chuncongcong.sw.service.RegionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.chuncongcong.framework.exception.ServiceErrorEnum.PARAM_ERROR;

@Service
public class ReginBizServiceImpl implements ReginBizService {

    private static final String MODE_POKEDEX = "pokedex";
    private static final String MODE_SCALE_WORLD = "scale_world";
    private static final Set<String> POKEDEX_HIDDEN_REGIONS = Set.of(
            "HISUI", "HISUI REGION", "\u6D17\u7FE0", "\u6D17\u7FE0\u5730\u533A"
    );
    private static final Set<String> SCALE_WORLD_HIDDEN_REGIONS = Set.of(
            "UNOVA", "UNOVA REGION", "\u5408\u4F17", "\u5408\u4F17\u5730\u533A",
            "ALOLA", "ALOLA REGION", "\u963F\u7F57\u62C9", "\u963F\u7F57\u62C9\u5730\u533A"
    );

    @Resource
    private RegionService regionService;
    @Resource
    private ItemBizService itemBizService;

    @Override
    public RegionVO info(RegionParam param) {
        if (Objects.isNull(param.getId())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        RegionDO regionDO = regionService.getById(param.getId());
        RegionVO regionVO = BeanUtil.copyProperties(regionDO, RegionVO.class);
        Map<Long, ReginItemCountVO> itemCountVOMap = itemBizService.userCountByRegions(Collections.singletonList(regionDO.getId()));
        ReginItemCountVO reginItemCountVO = itemCountVOMap.get(regionDO.getId());
        if (Objects.nonNull(reginItemCountVO)) {
            regionVO.setCount(reginItemCountVO.getCount());
            regionVO.setCollectCount(reginItemCountVO.getCollectCount());
        }
        return regionVO;
    }

    @Override
    public List<RegionVO> listAndCollect(RegionParam param) {
        String collectMode = Objects.nonNull(param) ? param.getCollectMode() : null;
        List<RegionDO> regionList = regionService.listOrderBySort().stream()
                .filter(regionDO -> visibleInCollectMode(regionDO, collectMode))
                .toList();
        List<RegionVO> regionVOList = new ArrayList<>();

        Map<Long, ReginItemCountVO> reginItemCountVOMap = itemBizService.userCountByRegions(regionList.stream().map(RegionDO::getId).toList());
        for (RegionDO regionDO : regionList) {
            RegionVO regionVO = BeanUtil.copyProperties(regionDO, RegionVO.class);
            ReginItemCountVO reginItemCountVO = reginItemCountVOMap.get(regionDO.getId());
            if (Objects.nonNull(reginItemCountVO)) {
                regionVO.setCount(reginItemCountVO.getCount());
                regionVO.setCollectCount(reginItemCountVO.getCollectCount());
            }
            regionVOList.add(regionVO);
        }
        return regionVOList;
    }

    private boolean visibleInCollectMode(RegionDO regionDO, String collectMode) {
        if (Objects.isNull(regionDO)) {
            return false;
        }
        Set<String> hiddenRegions;
        if (MODE_SCALE_WORLD.equals(collectMode)) {
            hiddenRegions = SCALE_WORLD_HIDDEN_REGIONS;
        } else if (MODE_POKEDEX.equals(collectMode)) {
            hiddenRegions = POKEDEX_HIDDEN_REGIONS;
        } else {
            return true;
        }
        return !hiddenRegions.contains(normalizeRegionKey(regionDO.getCode()))
                && !hiddenRegions.contains(normalizeRegionKey(regionDO.getName()));
    }

    private String normalizeRegionKey(String value) {
        return Objects.isNull(value) ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    @Override
    public ReginItemCountVO collectCount(RegionParam param) {
        if (Objects.nonNull(param.getId())) {
            Map<Long, ReginItemCountVO> reginItemCountVOMap = itemBizService.userCountByRegions(List.of(param.getId()));
            return reginItemCountVOMap.get(param.getId());
        }
        List<RegionDO> regionDOS = regionService.listOrderBySort();
        Map<Long, ReginItemCountVO> reginItemCountVOMap = itemBizService.userCountByRegions(regionDOS.stream().map(RegionDO::getId).toList());
        ReginItemCountVO reginItemCountVO = new ReginItemCountVO();
        Integer totalCount = 0;
        Integer totalCollectCount = 0;
        Integer totalUnCollectCount = 0;
        Integer totalCollectNumber = 0;
        BigDecimal totalCollectPrice = BigDecimal.ZERO;
        for (ReginItemCountVO value : reginItemCountVOMap.values()) {
            totalCount += value.getCount();
            totalCollectCount += value.getCollectCount();
            totalUnCollectCount += value.getUnCollectCount();
            totalCollectNumber += value.getCollectNumber();
            totalCollectPrice = totalCollectPrice.add(value.getCollectPriceTotal());
        }
        reginItemCountVO.setCount(totalCount);
        reginItemCountVO.setCollectCount(totalCollectCount);
        reginItemCountVO.setUnCollectCount(totalUnCollectCount);
        reginItemCountVO.setCollectRate(BigDecimal.valueOf(reginItemCountVO.getCollectCount())
                .divide(BigDecimal.valueOf(reginItemCountVO.getCount()), 4, RoundingMode.HALF_UP));
        reginItemCountVO.setCollectNumber(totalCollectNumber);
        reginItemCountVO.setCollectPriceTotal(totalCollectPrice);
        return reginItemCountVO;
    }
}