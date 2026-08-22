package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ItemCalendarParam {

    @Schema(description = "查询年份；不传时使用最新物品资讯的年份", example = "2026")
    private Integer year;
}
