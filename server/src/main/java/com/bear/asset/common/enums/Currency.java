package com.bear.asset.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {

    CNY("人民币"),
    HKD("港币"),
    USD("美元");

    private final String displayName;
}
