package com.chuncongcong.sw.biz.service;

import com.chuncongcong.framework.exception.ServiceException;
import com.chuncongcong.sw.bean.param.ItemCreateParam;
import com.chuncongcong.sw.bean.vo.ItemCreateVO;
import com.chuncongcong.sw.entity.ItemDO;
import com.chuncongcong.sw.entity.ItemInfoDO;
import com.chuncongcong.sw.entity.RegionDO;
import com.chuncongcong.sw.entity.SubItemDO;
import com.chuncongcong.sw.service.ItemInfoService;
import com.chuncongcong.sw.service.ItemService;
import com.chuncongcong.sw.service.RegionService;
import com.chuncongcong.sw.service.SubItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemCreateService {
    private final ItemService itemService;
    private final ItemInfoService itemInfoService;
    private final SubItemService subItemService;
    private final RegionService regionService;

    @Value("${file.pattern-prefix:/file/}")
    private String filePatternPrefix;

    @Transactional(rollbackFor = Exception.class)
    public ItemCreateVO create(ItemCreateParam param) {
        ItemEntryVerifier.verify(param.getVerificationCode());
        RegionDO region = regionService.getById(param.getRegionId());
        if (region == null) {
            throw new ServiceException(400, "所选地区不存在");
        }
        String code = ItemEntryNaming.itemCode(region, param.getCode(), param.getSort());
        boolean exists = itemService.lambdaQuery()
                .eq(ItemDO::getRegionId, param.getRegionId())
                .eq(ItemDO::getCode, code).exists();
        if (exists) {
            throw new ServiceException(400, "该地区下已存在相同编码的物品");
        }
        Set<String> subCodes = new HashSet<>();
        List<ItemCreateParam.SubItem> subItems = param.getSubItemList() == null ? List.of() : param.getSubItemList();
        List<String> resolvedSubCodes = new ArrayList<>();
        for (ItemCreateParam.SubItem sub : subItems) {
            String subCode = ItemEntryNaming.subCode(code, sub.getCode(), sub.getSort());
            if (!subCodes.add(subCode.toLowerCase(Locale.ROOT))) {
                throw new ServiceException(400, "子物品编码不能重复");
            }
            resolvedSubCodes.add(subCode);
        }

        ItemDO item = new ItemDO();
        item.setRegionId(param.getRegionId());
        item.setName(param.getName().trim());
        item.setCode(code);
        item.setImg(resolveImage(param.getImg(), region, code, false));
        item.setSort(param.getSort());
        item.setDeleted(0);
        ensureSaved(itemService.save(item));

        List<Long> infoIds = new ArrayList<>();
        for (ItemCreateParam.Info info : param.getInfoList()) {
            ItemInfoDO entity = new ItemInfoDO();
            entity.setItemId(item.getId());
            entity.setDate(info.getDate());
            entity.setType(info.getType());
            entity.setPrice(info.getPrice().trim());
            entity.setDeleted(0);
            ensureSaved(itemInfoService.save(entity));
            infoIds.add(entity.getId());
        }

        List<Long> subItemIds = new ArrayList<>();
        for (int index = 0; index < subItems.size(); index++) {
            ItemCreateParam.SubItem sub = subItems.get(index);
            SubItemDO entity = new SubItemDO();
            entity.setRegionId(item.getRegionId());
            entity.setItemId(item.getId());
            entity.setName(sub.getName().trim());
            entity.setCode(resolvedSubCodes.get(index));
            entity.setImg(resolveImage(sub.getImg(), region, entity.getCode(), true));
            entity.setSort(sub.getSort());
            entity.setDeleted(0);
            ensureSaved(subItemService.save(entity));
            subItemIds.add(entity.getId());
        }
        return new ItemCreateVO(item.getId(), infoIds, subItemIds);
    }

    private String resolveImage(String value, RegionDO region, String code, boolean subItem) {
        return value == null || value.isBlank() ? ItemEntryNaming.imageUrl(filePatternPrefix, region, code, subItem) : value.trim();
    }

    private void ensureSaved(boolean saved) {
        if (!saved) {
            throw new ServiceException("新增物品失败，请重试");
        }
    }
}
