package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "新品日历月份")
public class ItemCalendarMonthVO {

    @Schema(description = "月份", example = "8")
    private Integer month;

    @Schema(description = "当月发售、再版、定番条目")
    private List<ItemCalendarEntryVO> items = new ArrayList<>();
}
