package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PokemonParam {

    @Schema(description = "pokemon id")
    private Long id;

    @Schema(description = "user id")
    private Long userId;

    @Schema(description = "region id")
    private Long regionId;

    @Schema(description = "keyword")
    private String keyword;

    @Schema(description = "only collected")
    private Boolean collectedOnly;

    @Schema(description = "collectible type code")
    private String typeCode;
}
