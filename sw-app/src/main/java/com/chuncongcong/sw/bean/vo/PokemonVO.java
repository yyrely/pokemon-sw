package com.chuncongcong.sw.bean.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PokemonVO {

    private Long id;

    private Integer nationalNo;

    private Long regionId;

    private Integer generationNo;

    private String nameZh;

    private String nameEn;

    private String typeNames;

    private String img;

    private List<PokemonTypeVO> typeList;

    private Integer sort;

    private Boolean collect = Boolean.FALSE;

    private Integer collectRecordCount = 0;

    private BigDecimal collectPriceTotal = BigDecimal.ZERO;

    private List<UserPokemonCollectVO> collectList;

    private List<PokemonScaleRecordVO> scaleRecordList;
}
