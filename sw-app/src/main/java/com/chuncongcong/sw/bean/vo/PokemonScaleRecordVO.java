package com.chuncongcong.sw.bean.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PokemonScaleRecordVO {

    private Long sourceId;

    private String sourceType;

    private String itemName;

    private String itemImg;

    private Boolean collect = Boolean.FALSE;

    private LocalDate collectDate;

    private BigDecimal collectPrice;

    private String remark;
}
