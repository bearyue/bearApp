package com.bear.asset.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AssetCategory {

    LIQUID("流动资产"),
    INVESTMENT("投资资产"),
    RESTRICTED("受限资产"),
    PHYSICAL("实物资产"),
    LIABILITY("负债");

    private final String displayName;
}
