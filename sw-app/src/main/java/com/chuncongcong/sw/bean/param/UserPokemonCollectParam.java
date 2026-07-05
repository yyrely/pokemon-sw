package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserPokemonCollectParam {

    @Schema(description = "collect record id")
    private Long id;

    @Schema(description = "pokemon id")
    private Long pokemonId;

    @Schema(description = "collectible type code")
    private String collectibleTypeCode;

    @Schema(description = "collect date")
    private LocalDate collectDate;

    @Schema(description = "collect price")
    private BigDecimal collectPrice;

    @Schema(description = "remark")
    private String remark;

    @Schema(description = "exchange")
    private Boolean exchange;

    @Schema(description = "exchange remark")
    private String exchangeRemark;
}
