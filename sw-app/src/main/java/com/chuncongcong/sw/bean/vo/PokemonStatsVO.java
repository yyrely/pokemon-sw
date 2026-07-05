package com.chuncongcong.sw.bean.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PokemonStatsVO {

    private Long regionId;

    private Integer count = 0;

    private Integer collectCount = 0;

    private Integer collectNumber = 0;

    private BigDecimal collectRate = BigDecimal.ZERO;

    private BigDecimal collectPriceTotal = BigDecimal.ZERO;
}
