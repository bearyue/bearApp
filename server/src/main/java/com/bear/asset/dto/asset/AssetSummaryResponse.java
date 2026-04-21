package com.bear.asset.dto.asset;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AssetSummaryResponse {

    private String category;
    private String categoryName;
    private int count;
    private BigDecimal totalAmountCny;
}
