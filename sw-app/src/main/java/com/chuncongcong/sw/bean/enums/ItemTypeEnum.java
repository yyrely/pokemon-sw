package com.chuncongcong.sw.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author HU
 * @date 2025/11/8 11:27
 */

@Getter
@AllArgsConstructor
public enum ItemTypeEnum {

    ITEM(1, "主物品"),
    SUB_ITEM(2, "子物品"),
    ;

    private final Integer code;

    private final String desc;
}
