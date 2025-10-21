package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "地区信息")
public class RegionVO {

    @Schema(description = "地区ID", example = "1")
    private Long id;

    @Schema(description = "地区名称", example = "关都地区")
    private String name;

    @Schema(description = "地区编码", example = "KANTO")
    private String code;

    @Schema(description = "世代", example = "第一世代")
    private String generation;

    @Schema(description = "地区图片URL")
    private String img;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "物品总数")
    private Integer count = 0;

    @Schema(description = "物品已收集数量")
    private Integer collectCount = 0;

}
