package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PokedexScaleSyncResultVO {

    @Schema(description = "inserted derived pokedex collect count")
    private Integer insertCount = 0;

    @Schema(description = "source rows or sub-items skipped because no mapping exists")
    private Integer skipNoMappingCount = 0;

    @Schema(description = "sync status after operation")
    private Integer syncStatus = 0;

    public void incrementInsertCount() {
        insertCount = insertCount + 1;
    }

    public void incrementSkipNoMappingCount() {
        skipNoMappingCount = skipNoMappingCount + 1;
    }
}
