package com.bear.asset.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AssetSubType {

    // LIQUID
    CASH(AssetCategory.LIQUID, "现金"),
    DEMAND_DEPOSIT(AssetCategory.LIQUID, "活期存款"),
    MONEY_FUND(AssetCategory.LIQUID, "货币基金"),

    // INVESTMENT
    STOCK_A(AssetCategory.INVESTMENT, "A股"),
    STOCK_HK(AssetCategory.INVESTMENT, "港股"),
    STOCK_US(AssetCategory.INVESTMENT, "美股"),
    PUBLIC_FUND(AssetCategory.INVESTMENT, "公募基金"),
    BANK_FINANCIAL(AssetCategory.INVESTMENT, "银行理财"),
    TIME_DEPOSIT(AssetCategory.INVESTMENT, "定期存款"),
    LARGE_DEPOSIT(AssetCategory.INVESTMENT, "大额存单"),
    ADVISOR(AssetCategory.INVESTMENT, "投顾"),
    CUSTOM_INVESTMENT(AssetCategory.INVESTMENT, "自定义投资"),

    // RESTRICTED
    PROVIDENT_FUND(AssetCategory.RESTRICTED, "公积金"),
    INSURANCE(AssetCategory.RESTRICTED, "保险"),

    // PHYSICAL
    REAL_ESTATE(AssetCategory.PHYSICAL, "房产"),
    VEHICLE(AssetCategory.PHYSICAL, "车辆"),

    // LIABILITY
    MORTGAGE(AssetCategory.LIABILITY, "房贷"),
    CAR_LOAN(AssetCategory.LIABILITY, "车贷"),
    CREDIT_CARD(AssetCategory.LIABILITY, "信用卡");

    private final AssetCategory category;
    private final String displayName;
}
