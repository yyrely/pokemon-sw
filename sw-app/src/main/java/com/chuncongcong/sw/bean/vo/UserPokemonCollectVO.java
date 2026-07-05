package com.chuncongcong.sw.bean.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserPokemonCollectVO {

    private Long id;

    private Long pokemonId;

    private String collectibleTypeCode;

    private String collectibleTypeName;

    private String sourceName;

    private LocalDate collectDate;

    private BigDecimal collectPrice;

    private String remark;

    private Boolean exchange;

    private String exchangeRemark;
}
