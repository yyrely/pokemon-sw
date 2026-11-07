package com.chuncongcong.sw.biz.service;

import com.chuncongcong.framework.exception.ServiceException;
import com.chuncongcong.sw.entity.RegionDO;

import java.util.Locale;

/** Keeps codes, default image URLs and uploaded filenames consistent. */
public final class ItemEntryNaming {
    private ItemEntryNaming() {
    }

    public static String itemCode(RegionDO region, String code, Integer sort) {
        if (code == null || code.isBlank()) {
            if (region.getCode() == null || region.getCode().isBlank()) {
                throw new ServiceException(400, "所选地区未设置编码，请手动填写物品编码");
            }
            code = region.getCode().trim() + "_" + formatSort(sort);
        }
        return validateCode(code);
    }

    public static String subCode(String itemCode, String code, Integer sort) {
        return validateCode(code == null || code.isBlank() ? itemCode + "_" + formatSort(sort) : code);
    }

    public static String imageRelativePath(RegionDO region, String code, String extension, boolean subItem) {
        if (!java.util.List.of("jpg", "jpeg", "png").contains(extension)) {
            throw new ServiceException(400, "不支持的图片格式");
        }
        return "pic/region/" + safeSegment(region.getCode()) + (subItem ? "/sub/" : "/")
                + safeSegment(code) + "." + extension;
    }

    public static String imageUrl(String prefix, RegionDO region, String code, boolean subItem) {
        return imageUrl(prefix, region, code, "jpg", subItem);
    }

    public static String imageUrl(String prefix, RegionDO region, String code, String extension, boolean subItem) {
        return prefix.replaceAll("/+$", "") + "/" + imageRelativePath(region, code, extension, subItem);
    }

    private static String formatSort(Integer sort) {
        if (sort == null || sort < 1) {
            throw new ServiceException(400, "排序必须大于等于1");
        }
        return String.format(Locale.ROOT, "%02d", sort);
    }

    private static String validateCode(String code) {
        String value = code.trim();
        if (value.length() > 64) {
            throw new ServiceException(400, "生成的物品编码不能超过64个字符，请调整编码");
        }
        return value;
    }

    private static String safeSegment(String value) {
        if (value == null || !value.trim().matches("[\\p{L}\\p{N}_-]{1,64}")) {
            throw new ServiceException(400, "图片目录与文件名中的编码只能包含字母、数字、下划线或连字符");
        }
        return value.trim();
    }
}
