package com.chuncongcong.sw.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemCreateVO {
    private Long itemId;
    private List<Long> infoIds;
    private List<Long> subItemIds;
}
