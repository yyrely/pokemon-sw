package com.chuncongcong.sw.bean.param;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemImageUploadParam extends UploadParam {
    @NotNull(message = "请选择地区")
    @Positive(message = "地区ID必须为正整数")
    private Long regionId;

    @Size(max = 64, message = "物品编码不能超过64个字符")
    private String code;

    @NotNull(message = "请输入物品排序")
    @Min(value = 1, message = "排序必须大于等于1")
    private Integer sort = 1;

    @Size(max = 64, message = "子物品编码不能超过64个字符")
    private String subCode;

    @Min(value = 1, message = "排序必须大于等于1")
    private Integer subSort;
}
