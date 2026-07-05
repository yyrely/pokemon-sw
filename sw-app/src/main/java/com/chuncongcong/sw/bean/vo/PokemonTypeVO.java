package com.chuncongcong.sw.bean.vo;

import lombok.Data;

@Data
public class PokemonTypeVO {

    private Long id;

    private String code;

    private String nameZh;

    private String nameEn;

    private String iconUrl;

    private String localIcon;

    private String icon;

    private Integer sort;
}
