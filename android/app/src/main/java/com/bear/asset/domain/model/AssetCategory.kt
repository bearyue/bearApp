package com.bear.asset.domain.model

enum class AssetCategory(val displayName: String) {
    LIQUID("流动资产"),
    INVESTMENT("投资资产"),
    RESTRICTED("受限资产"),
    PHYSICAL("实体资产"),
    LIABILITY("负债")
}
