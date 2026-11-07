package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "新增物品及其发售信息、子物品")
public class ItemCreateParam {

    @NotBlank(message = "请输入验证码")
    private String verificationCode;

    @NotNull(message = "请选择地区")
    @Positive(message = "地区ID必须为正整数")
    private Long regionId;

    @NotBlank(message = "请输入物品名称")
    @Size(max = 100, message = "物品名称不能超过100个字符")
    private String name;

    @Size(max = 64, message = "物品编码不能超过64个字符")
    @Schema(description = "选填，留空时按地区编码_两位排序生成，例如 LumioseCity_01")
    private String code;

    @Size(max = 255, message = "物品图片地址不能超过255个字符")
    private String img;

    @NotNull(message = "请输入物品排序")
    @Min(value = 1, message = "排序必须大于等于1")
    private Integer sort = 1;

    @NotEmpty(message = "至少添加一条发售信息")
    @Size(max = 100, message = "发售信息不能超过100条")
    private List<@NotNull @Valid Info> infoList;

    @Size(max = 100, message = "子物品不能超过100个")
    private List<@NotNull @Valid SubItem> subItemList = new ArrayList<>();

    @Data
    public static class Info {
        @NotNull(message = "请选择发售日期")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;

        @NotNull(message = "请选择发售类型")
        @Min(value = 1, message = "发售类型不正确")
        @Max(value = 4, message = "发售类型不正确")
        @Schema(description = "1-发售，2-再版，3-定番，4-亚洲再版")
        private Integer type;

        @NotBlank(message = "请输入价格")
        @Size(max = 64, message = "价格不能超过64个字符")
        @Schema(description = "价格文本，支持货币单位，例如 5,500日元")
        private String price;
    }

    @Data
    public static class SubItem {
        @NotBlank(message = "请输入子物品名称")
        @Size(max = 100, message = "子物品名称不能超过100个字符")
        private String name;

        @Size(max = 64, message = "子物品编码不能超过64个字符")
        @Schema(description = "选填，留空时按主物品编码_两位子物品排序生成")
        private String code;

        @Size(max = 255, message = "子物品图片地址不能超过255个字符")
        private String img;

        @NotNull(message = "请输入子物品排序")
        @Min(value = 1, message = "排序必须大于等于1")
        private Integer sort = 1;
    }
}
