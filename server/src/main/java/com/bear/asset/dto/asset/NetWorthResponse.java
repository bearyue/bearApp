package com.bear.asset.dto.asset;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class NetWorthResponse {

    private BigDecimal totalAsset;
    private BigDecimal totalLiability;
    private BigDecimal netWorth;
    private List<AssetSummaryResponse> categoryBreakdown;
}
