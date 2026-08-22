package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "新品日历")
public class ItemCalendarVO {

    @Schema(description = "当前查询年份")
    private Integer year;

    @Schema(description = "最新物品资讯年份")
    private Integer latestYear;

    @Schema(description = "最新物品资讯月份")
    private Integer latestMonth;

    @Schema(description = "有物品资讯的年份，按倒序排列")
    private List<Integer> availableYears;

    @Schema(description = "1至12月的新品信息")
    private List<ItemCalendarMonthVO> months;
}
