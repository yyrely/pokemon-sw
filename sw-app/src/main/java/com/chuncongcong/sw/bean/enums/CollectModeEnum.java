package com.chuncongcong.sw.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CollectModeEnum {

    POKEDEX("pokedex", "宝可梦图鉴"),
    SCALE_WORLD("scale_world", "比例世界"),
    ;

    private final String code;

    private final String desc;

    public static boolean valid(String code) {
        for (CollectModeEnum value : values()) {
            if (value.code.equals(code)) {
                return true;
            }
        }
        return false;
    }
}
