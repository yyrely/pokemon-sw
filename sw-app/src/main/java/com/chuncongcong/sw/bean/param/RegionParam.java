package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "地区参数")
public class RegionParam {

    @Schema(description = "地区ID（更新时必传）")
    private Long id;

    @NotBlank(message = "地区名称不能为空")
    @Schema(description = "地区名称", example = "关都地区")
    private String name;

    @NotBlank(message = "地区编码不能为空")
    @Schema(description = "地区编码", example = "KANTO")
    private String code;

    @NotBlank(message = "世代不能为空")
    @Schema(description = "世代", example = "第一世代")
    private String generation;

    @Schema(description = "地区图片URL")
    private String img;

    @Schema(description = "排序", example = "1")
    private Integer sort;

}
