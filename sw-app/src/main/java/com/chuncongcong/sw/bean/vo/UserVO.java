package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "username")
    private String username;

    @Schema(description = "avatar")
    private String headImgUrl;

    @Schema(description = "contact")
    private String contact;

    @Schema(description = "collect mode")
    private String collectMode;

    @Schema(description = "onboarding version")
    private Integer onboardingVersion;

    @Schema(description = "pokedex sync status")
    private Integer pokemonCollectSyncStatus;

    @Schema(description = "pokedex sync time")
    private LocalDateTime pokemonCollectSyncTime;
}
