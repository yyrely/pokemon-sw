package com.chuncongcong.sw.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationPaymentTypeEnum {

    FULL_PAYMENT(1, "全款"),
    DEPOSIT(2, "定金"),
    ;

    private final Integer code;
    private final String desc;
}
